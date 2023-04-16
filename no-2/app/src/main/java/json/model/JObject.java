package json.model;

import java.util.ArrayList;
import java.util.List;

import json.JsonParserException;

public class JObject extends JValue {

    private List<JMember> members = new ArrayList<>();

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JObject other = (JObject) obj;
        return other.toString().equals(this.toString());
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {
        var buffer = new StringBuilder();
        buffer.append('{');
        var second = false;
        for (JMember jMember : members) {
            if (second) { buffer.append(','); }
            buffer.append(jMember.toString());
            second = true;
        }
        buffer.append('}');
        return buffer.toString();
    }

    public List<JMember> addMembers(List<JMember> newMembers) throws JsonParserException {
        for (JMember jMember : newMembers) {
            this.addMeber(jMember);
        }
        return this.members;
    }

    private JMember addMeber(JMember member) throws JsonParserException {
        for (JMember jMember : members) {
            if (jMember.getKey().equals(member.getKey())) {
                throw new JsonParserException(member.getKey() + " already exists in the members of this object");
            }
        }
        this.members.add(member);
        return member;
    }

    @Override
    public Object value() {
        return this;
    }
    

}
