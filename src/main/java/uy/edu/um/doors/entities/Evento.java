package uy.edu.um.doors.entities;
import uy.edu.um.tad.list.MyList;


public class Evento {
    private String tipo; //Puede ser CPU, RAM o DISK. misma consideracion q estado de proceso
    private MyList<String> instrucciones;


    public Evento(String tipo, MyList<String> instrucciones) {
        this.tipo = tipo;
        this.instrucciones = instrucciones;
    }


    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public MyList<String> getInstrucciones() {
        return instrucciones;
    }

    public void setInstrucciones(MyList<String> instrucciones) {
        this.instrucciones = instrucciones;
    }
}
