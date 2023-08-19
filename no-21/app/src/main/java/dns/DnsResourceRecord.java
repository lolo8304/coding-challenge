package dns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@SuppressWarnings({"SpellCheckingInspection", "FieldMayBeFinal", "FieldCanBeLocal", "unused"})
public class DnsResourceRecord {
    private String name;
    private int type;
    private int clazz;
    private long ttl;
    private int rdLength;
    private byte[] rData;
    private final Map<String, String> rDataValues;

    public static long getIpAddressLong(String ip) {
        var ipParts = ip.split("\\.");
        return
                Long.parseLong(ipParts[0]) * (1L^24)
                        + Long.parseLong(ipParts[1]) * (1L^16)
                        + Long.parseLong(ipParts[2]) * (1L^8)
                        + Long.parseLong(ipParts[3]);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public DnsResourceRecord(OctetReader reader) throws IOException {
        this.name = reader.readName().get();
        this.type = reader.readInt16().get();
        this.clazz = reader.readInt16().get();
        this.ttl = reader.readInt32().get();
        this.rdLength = reader.readInt16().get();
        this.rDataValues = new HashMap<>();
        if (this.rdLength > 0) {
            this.rData = reader.readBytes(this.rdLength).get();
            this.convertRDataToValues(reader);
        } else {
            this.rData = new byte[0];
        }
    }

    public boolean isIpAddress() {
        return this.type == HeaderFlags.QTYPE_A;
    }
    public boolean isNsWithIpAddress() {
        return this.type == HeaderFlags.QTYPE_NS && this.getRDataString("ADDRESS") != null;
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    private void convertRDataToValues(OctetReader topReader) throws IOException {
        if (this.rdLength > 0) {
            switch (this.type) {
                /* A <domain-name> which specifies the canonical or primary
                name for the owner.  The owner name is an alias.*/
                case HeaderFlags.QTYPE_CNAME -> {
                    convertRecordWithName(topReader, "CNAME");
                }
                /* CPU             A <character-string> which specifies the CPU type.
                   OS              A <character-string> which specifies the operating system type. */
                case HeaderFlags.QTYPE_HINFO -> {
                    convertRecordHInfo(topReader);
                }
                case HeaderFlags.QTYPE_NS -> {
                    convertRecordWithName(topReader, "NSDNAME");
                }
                /* PREFERENCE      A 16-bit integer which specifies the preference given to
                this RR among others at the same owner.  Lower values
                are preferred.
                EXCHANGE        A <domain-name> which specifies a host willing to act as
                a mail exchange for the owner name. */
                case HeaderFlags.QTYPE_MX -> {
                    convertRecordMx(topReader);
                }
                /* PTRDNAME        A <domain-name> which points to some location in the
                    domain name space. */
                case HeaderFlags.QTYPE_PTR -> {
                    convertRecordWithName(topReader, "PTRDNAME");
                }

                /*
                MNAME           The <domain-name> of the name server that was the
                                original or primary source of data for this zone.

                RNAME           A <domain-name> which specifies the mailbox of the
                                person responsible for this zone.

                SERIAL          The unsigned 32 bit version number of the original copy
                                of the zone.  Zone transfers preserve this value.  This
                                value wraps and should be compared using sequence space
                                arithmetic.

                REFRESH         A 32-bit time interval before the zone should be
                                refreshed.

                RETRY           A 32-bit time interval that should elapse before a
                                failed refresh should be retried.

                EXPIRE          A 32 bit time value that specifies the upper limit on
                                the time interval that can elapse before the zone is no
                                longer authoritative.
                 */
                case HeaderFlags.QTYPE_SOA -> {
                    convertRecordSoa(topReader);
                }
                /*
                TXT-DATA        One or more <character-string>s.
                 */
                case HeaderFlags.QTYPE_TXT -> {
                    convertRecordTxt(topReader);
                }
                /*
                ADDRESS         A 32 bit Internet address.
                                The RDATA section of
                                an A line in a master file is an Internet address expressed as four
                                decimal numbers separated by dots without any embedded spaces (e.g.,
                                "10.2.0.52" or "192.0.5.6").
                 */
                case HeaderFlags.QTYPE_A -> {
                    convertRecordIpAddress(topReader);
                }
                default -> {
                    var reader = new OctetReader(this.rData, topReader);
                    this.setRDataString(String.format("Unknown %d: %s", this.type, reader.readString(this.rData.length*2)));
                }
            }
        } else {
            this.setRDataString("");
        }
    }

    private void convertRecordHInfo(OctetReader topReader) throws IOException {
        var reader = new OctetReader(this.rData, topReader);
        var cpu = reader.readName().get();
        var os = reader.readName().get();
        this.rDataValues.put("CPU", cpu);
        this.rDataValues.put("OS", os);
        this.setRDataString(String.format("%s / %s", cpu, os));
    }

    private void convertRecordMx(OctetReader topReader) throws IOException {
        var reader = new OctetReader(this.rData, topReader);
        var preference = reader.readInt16().get();
        var exchange = reader.readQName().get();
        this.rDataValues.put("PREFERENCE", String.valueOf(preference));
        this.rDataValues.put("EXCHANGE", exchange);
        this.setRDataString(String.format("%d %s", preference, exchange));
    }

    private void convertRecordSoa(OctetReader topReader) throws IOException {
        var reader = new OctetReader(this.rData, topReader);
        var mName = reader.readQName().get();
        var rName = reader.readQName().get();
        var serial = reader.readInt32().get();
        var refresh = reader.readInt32().get();
        var retry = reader.readInt32().get();
        var expire = reader.readInt32().get();
        this.setRDataString(String.format("%s %s %d %d %d %d", mName, rName, serial, refresh, retry, expire));
        this.rDataValues.put("MNAME", mName);
        this.rDataValues.put("RNAME", rName);
        this.rDataValues.put("SERIAL", String.valueOf(serial));
        this.rDataValues.put("REFRESH", String.valueOf(refresh));
        this.rDataValues.put("RETRY", String.valueOf(retry));
        this.rDataValues.put("EXPIRE", String.valueOf(expire));
    }

    private void convertRecordTxt(OctetReader topReader) throws IOException {
        var reader = new OctetReader(this.rData, topReader);
        var txtList = new ArrayList<String>();
        var name = reader.readName();
        var i = 0;
        while (name.isPresent()) {
            txtList.add(name.get());
            this.rDataValues.put(String.valueOf(i), name.get());
            name = reader.readName();
            i++;
        }
        var txtListString = String.join("; ", txtList);
        this.setRDataString(txtListString);
        this.rDataValues.put("TXTDATA", txtListString);
    }

    private void convertRecordIpAddress(OctetReader topReader) throws IOException {
        var reader = new OctetReader(this.rData, topReader);
        var ip = reader.readIpAddress().get();
        this.setRDataString(ip);
        this.rDataValues.put("ADDRESS", ip);
    }

    private void convertRecordWithName(OctetReader topReader, String nameKey) throws IOException {
        var reader = new OctetReader(this.rData, topReader);
        var qName = reader.readQName().get();
        this.setRDataString(qName);
        this.rDataValues.put(nameKey, qName);
    }

    public void setRDataString(String data) {
        this.rDataValues.put("DATA", data);
    }
    public void setRDataValue(String key, String data){
        this.rDataValues.put(key, data);
    }

    public String getRDataString() {
        return this.getRDataString("DATA");
    }
    public String getRDataString(String key) {
        return this.rDataValues.get(key);
    }

    public Optional<String> getIpAddress() {
        if (this.type == HeaderFlags.QTYPE_A) {
            return Optional.of(this.getRDataString());
        } else {
            return Optional.empty();
        }
    }

    public long getIpAddressLong() {
        var ip = this.getIpAddress();
        return ip.map(DnsResourceRecord::getIpAddressLong).orElse(0L);
    }

    public String getName() {
        return name;
    }


    public int getType() { return type; }
    public int getClazz() { return clazz; }

    public boolean isAuthorityName(DnsServer.Name dnsName) {
        return this.getAuthorityName().equals(dnsName);
    }

    public DnsServer.Name getAuthorityName() {
        return DnsServer.Name.fromName(this.getRDataString("NSDNAME"), this.getRDataString("ADDRESS"));
    }
}