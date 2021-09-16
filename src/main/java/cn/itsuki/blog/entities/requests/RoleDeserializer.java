package cn.itsuki.blog.entities.requests;

import cn.itsuki.blog.entities.Role;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * role 解析成 json
 *
 * @author: itsuki
 * @create: 2021-09-16 09:09
 **/
public class RoleDeserializer extends JsonDeserializer<Role> {
    @Override
    public Role deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode node = codec.readTree(jsonParser);

        if (node == null) {
            return null;
        }

        String text = node.textValue();
        if (StringUtils.isEmpty(text)) {
            return null;
        }

        for (Role role : Role.values()) {
            if (text.equals(role.toString())) {
                return role;
            }
        }

        throw new IllegalArgumentException(
                "role must be in " + StringUtils.arrayToDelimitedString(Role.values(), ", "));
    }
}
