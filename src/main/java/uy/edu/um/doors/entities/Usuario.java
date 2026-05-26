package uy.edu.um.doors.entities;

public class Usuario {

    private String uid;
    private String alias;
    private String tipo;  //Puede ser admin o generic. Como en el csv siempe aparece "ADMIN" o "GENERIC", no hay problemas con las mayusuculas y minisculas.

    public Usuario(String uid, String alias, String tipo) {
        this.uid = uid;
        this.alias = alias;
        this.tipo = tipo;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
