package uy.edu.um.doors.entities;

import org.junit.jupiter.api.Test;
import uy.edu.um.tad.list.MyLinkedListImpl;
import uy.edu.um.tad.list.MyList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProcesoTest {
    @Test
    void calcularPrioridad_admin() {
        Usuario admin = new Usuario("1", "Lolo", "ADMIN");

        MyList<Evento> eventos = new MyLinkedListImpl<>();
        MyList<String> instrucciones = new MyLinkedListImpl<>();
        instrucciones.add("add");
        instrucciones.add("sub");
        eventos.add(new Evento("CPU", instrucciones));

        Proceso p = new Proceso("P1", "test.exe", 0, admin, "NEW", eventos);
        assertEquals(40, p.calcularPrioridad());
    }
}