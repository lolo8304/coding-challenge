package dns;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;

public class DnsMessage {
    private int id;

    private final int flags;

    private final List<DnsQuestion> questions = new ArrayList<>();

    private final List<DnsResourceRecord> answers = new ArrayList<>();

    private final List<DnsResourceRecord> authorities = new ArrayList<>();

    private final List<DnsResourceRecord> additionalRecords = new ArrayList<>();

    public DnsMessage() {
        this(OctetHelper.generate16BitIdentifier(), dns.Flags.QR_QUERY);
    }
    public DnsMessage(int additionalFlags) {
        this(OctetHelper.generate16BitIdentifier(), dns.Flags.QR_QUERY | additionalFlags);
    }

    public DnsMessage(int id, int flags) {
        this.id = id;
        this.flags = flags;
    }

    public DnsMessage(DnsMessage request) {
        this();
        this.id = request.getId();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public DnsMessage(OctetReader reader) throws IOException {
        this.id = reader.readInt16().get();
        this.flags = reader.readInt16().get();

        int questionsCount = reader.readInt16().get();
        int answersCount = reader.readInt16().get();
        int authoritiesCount = reader.readInt16().get();
        int additionalCount = reader.readInt16().get();

        for (int i = 0; i < questionsCount; i++) {
            this.questions.add(new DnsQuestion(reader));
        }
        for (int i = 0; i < answersCount; i++) {
            this.answers.add(new DnsResourceRecord(reader));
        }
        for (int i = 0; i < authoritiesCount; i++) {
            this.authorities.add(new DnsResourceRecord(reader));
        }
        for (int i = 0; i < additionalCount; i++) {
            var addOn = new DnsResourceRecord(reader);
            this.additionalRecords.add(addOn);
            if (addOn.isIpAddress()) {
                var authorityRecord = getAuthorityByName(addOn.getName());
                authorityRecord.ifPresent(dnsResourceRecord -> dnsResourceRecord.setRDataValue("ADDRESS", addOn.getRDataString("ADDRESS")));
            }
        }
    }

    private Optional<DnsResourceRecord> getAuthorityByName(String name) {
        for(var authority : this.getAuthorities()) {
            if (authority.getRDataString().equalsIgnoreCase(name)) {
                return Optional.of(authority);
            }
        }
        return Optional.empty();
    }

    public int getId() {
        return id;
    }

    public int getFlags() {
        return flags;
    }

    public int getQuestionCount() {
        return this.questions.size();
    }

    public int getAnswersCount() {
        return this.answers.size();
    }

    public int getAuthorityCount() {
        return this.authorities.size();
    }

    public int getAdditionalCount() {
        return this.additionalRecords.size();
    }

    public DnsMessage addQuestion(DnsQuestion question) {
        this.questions.clear();
        this.questions.add(question);
        return this;
    }

    public List<DnsQuestion> getQuestions() {
        return questions;
    }

    public List<DnsResourceRecord> getAnswers() {
        return answers;
    }

    public boolean hasIpAddress() {
        for (var r : this.getAnswers()) {
            if (r.isIpAddress() && r.getName().equalsIgnoreCase(this.getQuestions().get(0).getName())) return true;
        }
        return false;
    }

    public boolean hasAnswerOf(int type) {
        if (type == HeaderFlags.QTYPE_A) return this.hasIpAddress();
        for (var r : this.getAnswers()) {
            if (r.getType() == type) return true;
        }
        return false;
    }

    public List<String> getIpAddresses() {
        var list = new ArrayList<>(this.getAnswers());
        list.sort(Comparator.comparingLong(DnsResourceRecord::getIpAddressLong));
        return list.stream().map(DnsResourceRecord::getIpAddress).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toList());
    }
    public Optional<String> getCName() {
        for (var r : this.getAnswers()) {
            if (r.getType() == HeaderFlags.QTYPE_CNAME) {
                return Optional.of(r.getRDataString());
            }
        }
        return Optional.empty();
    }

    public List<DnsResourceRecord> getAuthorities() {
        return authorities;
    }

    @SuppressWarnings("unused")
    public List<DnsResourceRecord> getAdditionalRecords() {
        return additionalRecords;
    }

    public OctetWriter writeHeader(OctetWriter writer) {
        return writer.appendInt16(this.id)
        .appendInt16(this.flags)
        .appendInt16(this.getQuestionCount())
        .appendInt16(this.getAnswersCount())
        .appendInt16(this.getAuthorityCount())
        .appendInt16(this.getAdditionalCount());
    }

    public StringBuilder debugLog(StringBuilder builder) {
        builder.append("ID=").append(this.id)
                .append("flags: [").append(String.join(",", Flags.flags(this.flags))).append("]");
        builder.append(", Count [Q:").append(this.getQuestionCount())
                .append(", A:").append(this.getAnswersCount())
                .append(", AUTH:").append(this.getAuthorityCount())
                .append(", ADD:").append(this.getAdditionalCount()).append("]").append("\n");

        for (var q: this.getQuestions()) {
            q.debugLog(builder);
        }
        for (var a: this.getAnswers()) {
            a.debugLog(builder);
        }
        for (var a: this.getAuthorities()) {
            a.debugLog(builder);
        }
        for (var a: this.getAdditionalRecords()) {
            a.debugLog(builder);
        }
        return builder;
    }

    public OctetWriter write(OctetWriter writer) {
        writer = this.writeHeader(writer);
        for (DnsQuestion question : this.questions) {
            writer = question.write(writer);
        }
        return writer;
    }

    @SuppressWarnings("unused")
    public String send(String dnsServer, int port) throws IOException {
        var hexMessageToSend = this.write(new OctetWriter()).toString();
        return transfer(dnsServer, port, hexMessageToSend).write(new OctetWriter()).toString();
    }

    private DnsMessage transfer(String dnsServer, int port, String hexMessageToSend) throws IOException {
        var server = new DnsServer(dnsServer, port);
        var hexMessageReceived = server.sendAndReceive(hexMessageToSend);
        return new DnsResponseMessage(this, hexMessageReceived);
    }

    @SuppressWarnings("unused")
    public DnsResourceRecord getRandomAuthorityWithIp() {
        var nsListWithIpAddress = this.getAuthorities().stream().filter(DnsResourceRecord::isNsWithIpAddress).collect(Collectors.toList());
        return nsListWithIpAddress.get(new SecureRandom().nextInt(nsListWithIpAddress.size()));
    }
    public DnsResourceRecord getRandomAuthority() {
        return this.getAuthorities().get(new SecureRandom().nextInt(this.getAuthorityCount()));
    }

    public boolean hasAuthority(DnsServer.Name dnsName) {
        return this.getAuthorities().stream().anyMatch( (x) -> x.isAuthorityName(dnsName));
    }

}
