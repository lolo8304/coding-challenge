package dns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DnsResourceRecord {
    private String name;
    private int type;
    private int clazz;
    private long ttl;
    private int rdLength;
    private byte[] rData;
    private Map<String, String> rDataValues;

    public static long getIpAddressLong(Optional<String> ip) {
        if (ip.isPresent()) {
            var ipParts = ip.get().split("\\.");
            return
                    Long.parseLong(ipParts[0]) * (1L^24)
                            + Long.parseLong(ipParts[1]) * (1L^16)
                            + Long.parseLong(ipParts[2]) * (1L^8)
                            + Long.parseLong(ipParts[3]);
        } else {
            return 0L;
        }
    }

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

    private void convertRDataToValues(OctetReader topReader) throws IOException {
        if (this.rdLength > 0) {
            switch (this.type) {
                /* A <domain-name> which specifies the canonical or primary
                name for the owner.  The owner name is an alias.*/
                case DnsMessage.HeaderFlags.QTYPE_CNAME -> {
                    var cName = Name.fromOctetBytes(this.rData);
                    this.setRDataString(cName);
                    this.rDataValues.put("CNAME", cName);
                }
                /* CPU             A <character-string> which specifies the CPU type.
                   OS              A <character-string> which specifies the operating system type. */
                case DnsMessage.HeaderFlags.QTYPE_HINFO -> {
                    var reader = new OctetReader(this.rData, topReader);
                    var cpu = reader.readName().get();
                    var os = reader.readName().get();
                    this.rDataValues.put("CPU", cpu);
                    this.rDataValues.put("OS", os);
                    this.setRDataString(String.format("%s / %s", cpu, os));
                }
                case DnsMessage.HeaderFlags.QTYPE_NS -> {
                    var reader = new OctetReader(this.rData, topReader);
                    var nsDName = reader.readQName().get();
                    this.setRDataString(nsDName);
                    this.rDataValues.put("NSDNAME", nsDName);
                }
                /* PREFERENCE      A 16 bit integer which specifies the preference given to
                this RR among others at the same owner.  Lower values
                are preferred.
                EXCHANGE        A <domain-name> which specifies a host willing to act as
                a mail exchange for the owner name. */
                case DnsMessage.HeaderFlags.QTYPE_MX -> {
                    var reader = new OctetReader(this.rData, topReader);
                    var preference = reader.readInt16().get();
                    var exchange = reader.readQName().get();
                    this.rDataValues.put("PREFERENCE", String.valueOf(preference));
                    this.rDataValues.put("EXCHANGE", exchange);
                    this.setRDataString(String.format("%d %s", preference, exchange));
                }
                /* PTRDNAME        A <domain-name> which points to some location in the
                    domain name space. */
                case DnsMessage.HeaderFlags.QTYPE_PTR -> {
                    var reader = new OctetReader(this.rData, topReader);
                    var ptrDName = reader.readQName().get();
                    this.setRDataString(ptrDName);
                    this.rDataValues.put("PTRDNAME", ptrDName);
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

                REFRESH         A 32 bit time interval before the zone should be
                                refreshed.

                RETRY           A 32 bit time interval that should elapse before a
                                failed refresh should be retried.

                EXPIRE          A 32 bit time value that specifies the upper limit on
                                the time interval that can elapse before the zone is no
                                longer authoritative.
                 */
                case DnsMessage.HeaderFlags.QTYPE_SOA -> {
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
                /*
                TXT-DATA        One or more <character-string>s.
                 */
                case DnsMessage.HeaderFlags.QTYPE_TXT -> {
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
                /*
                ADDRESS         A 32 bit Internet address.
                                The RDATA section of
                                an A line in a master file is an Internet address expressed as four
                                decimal numbers separated by dots without any embedded spaces (e.g.,
                                "10.2.0.52" or "192.0.5.6").
                 */
                case DnsMessage.HeaderFlags.QTYPE_A -> {
                    var reader = new OctetReader(this.rData, topReader);
                    var ip = reader.readIpAddress().get();
                    this.setRDataString(ip);
                    this.rDataValues.put("ADDRESS", ip);
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

    private void setRDataString(String data) {
        this.rDataValues.put("DATA", data);
    }

    public String getRDataString() {
        return this.getRDataString("DATA");
    }
    public String getRDataString(String key) {
        return this.rDataValues.get(key);
    }

    public Optional<String> getIpAddress() {
        if (this.type == DnsMessage.HeaderFlags.QTYPE_A) {
            return Optional.of(this.getRDataString());
        } else {
            return Optional.empty();
        }
    }

    public long getIpAddressLong() {
        return getIpAddressLong(this.getIpAddress());
    }

    public String getName() {
        return name;
    }
    public int getType() {
        return type;
    }
    public int getClazz() {
        return clazz;
    }
    public long getTtl() {
        return ttl;
    }
    public int getRdLength() {
        return rdLength;
    }
    public byte[] getRData() {
        return rData;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setType(int type) {
        this.type = type;
    }
    public void setClazz(int clazz) {
        this.clazz = clazz;
    }
    public void setTtl(long ttl) {
        this.ttl = ttl;
    }
    public void setRdLength(int rdLength) {
        this.rdLength = rdLength;
    }
    public void setrData(byte[] rData) {
        this.rData = rData;
    }


}