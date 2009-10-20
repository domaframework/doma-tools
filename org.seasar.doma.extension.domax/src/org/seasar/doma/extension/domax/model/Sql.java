package org.seasar.doma.extension.domax.model;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

public class Sql {

    private final String sql;

    public Sql(String sql) {
        this.sql = sql;
    }

    public InputStream getAsInputStream() {
        return new ByteArrayInputStream(sql.getBytes(Charset.forName("UTF-8")));
    }
}
