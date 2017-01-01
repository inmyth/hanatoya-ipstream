package jp.hanatoya.ipcam.repo;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.NotNull;
import org.parceler.Parcel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "SWITCH".
 */
@Parcel
@Entity
public class Switch {

    @Id(autoincrement = true)
     Long id;

    @NotNull
     String name;

    @NotNull
     String cgi;
     int port;
     long camId;

    @Generated
    public Switch() {
    }

    public Switch(Long id) {
        this.id = id;
    }

    @Generated
    public Switch(Long id, String name, String cgi, int port, long camId) {
        this.id = id;
        this.name = name;
        this.cgi = cgi;
        this.port = port;
        this.camId = camId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    public String getName() {
        return name;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getCgi() {
        return cgi;
    }

    /** Not-null value; ensure this value is available before it is saved to the database. */
    public void setCgi(@NotNull String cgi) {
        this.cgi = cgi;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public long getCamId() {
        return camId;
    }

    public void setCamId(long camId) {
        this.camId = camId;
    }

}
