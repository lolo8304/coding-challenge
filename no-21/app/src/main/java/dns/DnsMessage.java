package dns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public DnsMessage(int id, int flags) {
        this.id = id;
        this.flags = flags;
    }

    public DnsMessage(DnsMessage request) {
        this();
        this.id = request.getId();
    }

    public DnsMessage(OctetReader reader) throws IOException {
        this.id = reader.readInt16().get();
        this.flags = reader.readInt16().get();

        var questionsCount = reader.readInt16().get();
        var answersCount = reader.readInt16().get();
        var authoritiesCount = reader.readInt16().get();
        var additionalCount = reader.readInt16().get();

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
            this.additionalRecords.add(new DnsResourceRecord(reader));
        }
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

    public int getAnswerCount() {
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

    public List<String> getIpAddresses() {
        var list = new ArrayList<>(this.answers);
        list.addAll(this.additionalRecords);
        list.sort( (x,y) -> {
            return Long.compare(x.getIpAddressLong(), y.getIpAddressLong());
        });
        return list.stream().map(DnsResourceRecord::getIpAddress).filter(Optional::isPresent).map(Optional::get).toList();
    }

    public List<DnsResourceRecord> getAuthorities() {
        return authorities;
    }

    public List<DnsResourceRecord> getAdditionalRecords() {
        return additionalRecords;
    }

    public OctetWriter writeHeader(OctetWriter writer) {
        return writer.appendInt16(this.id)
        .appendInt16(this.flags)
        .appendInt16(this.getQuestionCount())
        .appendInt16(this.getAnswerCount())
        .appendInt16(this.getAuthorityCount())
        .appendInt16(this.getAdditionalCount());
    }

    public OctetWriter write(OctetWriter writer) throws IOException {
        writer = this.writeHeader(writer);
        for (int i = 0; i < this.questions.size(); i++) {
            writer = this.questions.get(i).write(writer);
        }
        return writer;
    }

    public String send(String dnsServer, int port) throws IOException {
        var hexMessageToSend = this.write(new OctetWriter()).toString();
        return transfer(dnsServer, port, hexMessageToSend).write(new OctetWriter()).toString();
    }

    private DnsMessage transfer(String dnsServer, int port, String hexMessageToSend) throws IOException {
        var server = new DnsServer(dnsServer, port);
        var hexMessageReceived = server.sendAndReceive(hexMessageToSend);
        return new DnsResponseMessage(this, hexMessageReceived);
    }

}
