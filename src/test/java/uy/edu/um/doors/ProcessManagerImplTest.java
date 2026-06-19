package uy.edu.um.doors;

import org.junit.jupiter.api.Test;
import uy.edu.um.doors.entities.Evento;
import uy.edu.um.doors.entities.Proceso;
import uy.edu.um.doors.entities.Usuario;
import uy.edu.um.tad.list.MyLinkedListImpl;
import uy.edu.um.tad.list.MyList;

import static org.junit.jupiter.api.Assertions.*;

class ProcessManagerImplTest {

    @Test
    void loadProcessAndUserData() {
        ProcessManagerImpl processManager = new ProcessManagerImpl();

        Usuario lolo = new Usuario("1", "Lolo", "ADMIN");

        String[] instrucciones = {"add","sub"};
        MyList<Evento> eventos = new MyLinkedListImpl<>();
        eventos.add(new Evento("CPU", instrucciones));

        Proceso proceso = new Proceso("P1", "test.exe", 0, lolo, "NEW", eventos);
        processManager.getProcesosNuevos().enqueue(proceso);

        assertFalse(processManager.getProcesosNuevos().isEmpty());
    }

    @Test
    void prepareProcesses() {
        ProcessManagerImpl processManager = new ProcessManagerImpl();

        Usuario lolo = new Usuario("1", "Lolo", "ADMIN");

        String[] instrucciones = {"add"};
        MyList<Evento> eventos = new MyLinkedListImpl<>();
        eventos.add(new Evento("CPU", instrucciones));
        Proceso proceso = new Proceso("P1", "test.exe", 0, lolo, "NEW", eventos);

        processManager.getProcesosNuevos().enqueue(proceso);
        processManager.prepareProcesses();

        assertTrue(processManager.getProcesosNuevos().isEmpty());
        assertEquals(1, processManager.getProcesosPendientes().size());
    }

    @Test
    void executeNextProcess() {
        ProcessManagerImpl processManager = new ProcessManagerImpl();

        Usuario lolo = new Usuario("1", "Lolo", "ADMIN");

        String[] instrucciones = {"add"};
        MyList<Evento> eventos = new MyLinkedListImpl<>();
        eventos.add(new Evento("CPU", instrucciones));
        Proceso proceso = new Proceso("P1", "test.exe", 0, lolo, "NEW", eventos);

        processManager.getProcesosNuevos().enqueue(proceso);
        processManager.prepareProcesses();
        processManager.executeNextProcess();

        assertNotNull(processManager.getProcesoEjecutado());
        assertEquals("RUNNING", processManager.getProcesoEjecutado().getEstado());
    }

    @Test
    void finishProcessOk() {
        ProcessManagerImpl processManager = new ProcessManagerImpl();

        Usuario lolo = new Usuario("1", "Lolo", "ADMIN");
        String[] instrucciones = {"add"};
        MyList<Evento> eventos = new MyLinkedListImpl<>();
        eventos.add(new Evento("CPU", instrucciones));
        Proceso p = new Proceso("P1", "test.exe", 0, lolo, "NEW", eventos);

        processManager.getProcesosNuevos().enqueue(p);
        processManager.prepareProcesses();
        processManager.executeNextProcess();
        processManager.finishProcessOk();

        assertNull(processManager.getProcesoEjecutado());
        assertFalse(processManager.getProcesosFinalizados().isEmpty());
        assertEquals("FINISHED", processManager.getProcesosFinalizados().peek().getEstado());
        assertEquals("OK", processManager.getProcesosFinalizados().peek().getFinalizacion());
    }

}