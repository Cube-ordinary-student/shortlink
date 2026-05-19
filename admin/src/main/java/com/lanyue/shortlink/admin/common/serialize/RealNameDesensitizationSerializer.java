package com.lanyue.shortlink.admin.common.serialize;

import cn.hutool.core.util.DesensitizedUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class RealNameDesensitizationSerializer extends JsonSerializer<String> {
    @Override
    public void serialize(String realName, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String realNameDesensitization = DesensitizedUtil.chineseName(realName);
        jsonGenerator.writeString(realNameDesensitization);

    }
}
