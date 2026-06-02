package uy.edu.um.doors;

import lombok.Getter;
import uy.edu.um.doors.entities.Evento;
import uy.edu.um.doors.entities.Logger;
import uy.edu.um.doors.entities.Proceso;
import uy.edu.um.doors.entities.Usuario;

import uy.edu.um.tad.hash.MyHash;
import uy.edu.um.tad.hash.MyHashImpl;
import uy.edu.um.tad.heap.EmptyHeapException;
import uy.edu.um.tad.heap.MyHeap;
import uy.edu.um.tad.heap.MyHeapImpl;
import uy.edu.um.tad.list.MyLinkedListImpl;
import uy.edu.um.tad.list.MyList;
import uy.edu.um.tad.queue.EmptyQueueException;
import uy.edu.um.tad.queue.MyQueue;
import uy.edu.um.tad.queue.MyQueueImpl;
import uy.edu.um.tad.stack.EmptyStackException;
import uy.edu.um.tad.stack.MyStack;
import uy.edu.um.tad.stack.MyStackImpl;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;

@Getter
public class ProcessManagerImpl implements ProcessManager {

    //EL DISEÑO DE LA ESTRUCTURA DE ALMACENAMIENTO DEBE IMPLEMENTARSE EN ESTA CLASE EN RELACIÓN CON LAS ENTIDADES QUE DEFINA


    private final MyQueue<Proceso> procesosNuevos = new MyQueueImpl<Proceso>();

    private final MyHeap<Proceso> procesosPendientes = new MyHeapImpl<Proceso>(false);

    private final MyStack<Proceso> procesosFinalizados = new MyStackImpl<Proceso>();

    private final MyHash<String, Usuario> usuarios = new MyHashImpl<String, Usuario>();

    private final Logger logger = new Logger();

    private Proceso procesoEjecutado = null;
    //running process


    @Override
    public void loadProcessAndUserData(String processCsvPath, String usersCsvPath) {

        try {
            // -_-_-_- CARGAR USUARIOS -_-_-_-

            BufferedReader brU = new BufferedReader(new FileReader(usersCsvPath)); // Hace cosas con el archivo para poder leer una línea a la vez
// todo - envuelve el archivo en un 'FileReader' que abre el archivo y permite que BufferedReader lea una línea a la vez

            brU.readLine();        // descarta la cabecera, no la guarda en ninguna variable porque no nos interesa
            String lineaU = brU.readLine(); // lee la primer línea CON DATOS (no la cabecera) y se prepara para la próxima (el cursor se 'para' sobre la siguiente)

            if (lineaU == null) {         // todo chequeo innecesario porque no nos van a pasar archivos vacíos pero me gusta hacerlo igual jej
                System.out.println("El archivo de usuarios está vacío");
                return;
            }

            while (lineaU != null) { //recorro todas las líneas arrancando en la primera

                // P1 - separamos cada atributo
                String[] atributosU = lineaU.split(";", 3);

                // P2 - guardamos los atributos en variables
                String uid = atributosU[0];
                String alias = atributosU[1];
                String tipo = atributosU[2];

                // P3 - creamos y guardamos usuario en el hash
                Usuario nuevoUsuario = new Usuario(uid, alias, tipo);
                this.usuarios.put(uid, nuevoUsuario);

                // P4 - leemos la próxima linea
                lineaU = brU.readLine();
            }
            brU.close();


            // -_-_-_- CARGAR PROCESOS -_-_-_-

            BufferedReader brP = new BufferedReader(new FileReader(processCsvPath));

            brP.readLine();        // lee cabecera (uid, alias, tipo) y para el cursor sobre la próxima línea
            String lineaP = brP.readLine();      // lee la primer línea CON DATOS y se prepara para la próxima (el cursor se 'para' sobre la siguiente)


            while (lineaP != null) {     // recorro todas las líneas de procesos

                // P1 - separamos cada atributo
                String[] atributosP = lineaP.split(";", 4);

                // P2 - guardamos los atributos en variables
                String pid = atributosP[0];
                String uidP = atributosP[1];
                String pname = atributosP[2];
                String stringEventos = atributosP[3];

                // P2.1 - consigo el usuario ya existente en los datos
                Usuario usuarioP = usuarios.get(uidP);

                // P2.2 - arreglo los eventos de cada proceso (separo tipo de evento de las instrucciones y lo guardo en lista 'eventos')

                stringEventos = stringEventos.substring(1, stringEventos.length() - 1); // saca el '{' del principio y el '}' del final

                String[] partesEventos = stringEventos.split("# ");      // separo por '#' cada evento individualmente

                MyList<Evento> eventos = new MyLinkedListImpl<>();          // creo lista de eventos

                for (String parteEv : partesEventos) {
                    // separo por ':[' el tipo de evento y las instrucciones
                    String[] tipoEInstrucciones = parteEv.split(":\\[");      // separo por ':\\[' porque los corchetes '[' tienen un significado especial. Con \\ le digo: quiero el corchete literal, no el especial.

                    // guardo at. en variables
                    String tipoEv = tipoEInstrucciones[0]; // puede ser DISK, CPU, RAM...
                    String instruccionesString = tipoEInstrucciones[1]; // queda todas las instrucciones en un mismo string pero separadas por ',' y con un ']' al final (que sobra)
                    instruccionesString = instruccionesString.substring(0, instruccionesString.length() - 1);    // saco el ']' que sobraba

                    MyList<String> instrucciones = new MyLinkedListImpl<>();    // creo lista para guardar las instrucciones
                    String[] instruccionesArray = instruccionesString.split(", ");         // creo array de strings con las instrucciones (que separo por ', ')

                    for (String instruccion : instruccionesArray) {     // guardo de a una las instrucciones en la lista instrucciones
                        instrucciones.add(instruccion);
                    }

                    Evento nuevoEv = new Evento(tipoEv, instrucciones);         // creo el nuevo evento
                    eventos.add(nuevoEv);      // guardo el nuevo evento
                }
                Proceso nuevoPr = new Proceso(pid, pname, 0, usuarioP, "NEW", eventos);     // creo el proceso nuevo con prioridad = 0 porque la prioridad real se calcula luego en 'prepareProcess'
                procesosNuevos.enqueue(nuevoPr);        // lo cargo en la fila de procesos

                lineaP = brP.readLine();
            }
            brP.close();      // .close() 'libera' el archivo


            System.out.println("Se cargaron los archivos correctamente");

        } catch (
                IOException e) {           // Java obliga a manejar esas exceptions cuando uso BufferedReader y FileReader. Si no pongo el try-catch, el código no compila.
            System.out.println("Error al leer archivos");
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void prepareProcesses() {

        while (!procesosNuevos.isEmpty()) {

            Proceso proceso = null;
            try {
                proceso = procesosNuevos.dequeue();

            } catch (EmptyQueueException e) {

                System.out.println("no hay procesos para preparar");
                ;
            }

            proceso.setPrioridad(proceso.calcularPrioridad());

            proceso.setEstado("PENDIENTE");

            procesosPendientes.insert(proceso);

            logger.logpPrepare(proceso);
            System.out.println(" Se han preparado correctamente los procesos");
        }


    }

    @Override
    public void executeNextProcess() {
        try {
            procesoEjecutado = procesosPendientes.remove();
        } catch (EmptyHeapException e) {
            System.out.println(" No hay procesos pendientes para ejecutar");
        }

        procesoEjecutado.setEstado("RUNNING");

        logger.logExecute(procesoEjecutado);

        System.out.println("Se inicio la ejecucion del proceso correctamente");
    }

    @Override
    public void finishProcessOk() {

        if (procesosFinalizados.size() == 500) { //Asumimos q 500 es el limite del sistema

            logger.logOverflow();

            try {

                while (!procesosFinalizados.isEmpty()) {
                    Proceso proceso = procesosFinalizados.pop();
                    logger.logProcesoOverflow(proceso);
                }

            } catch (EmptyStackException e) {
                System.out.println("El stack esta vacio");
            }
        }

        procesoEjecutado.setEstado("FINISHED");
        procesoEjecutado.setFinalizacion("OK");
        procesosFinalizados.push(procesoEjecutado);

        logger.logFinishOK(procesoEjecutado);

        procesoEjecutado = null;

        System.out.println("El proceso finalizo correctamente");

    }

    @Override
    public void finishProcessError() {
        if (procesosFinalizados.size() == 500) { //Asumimos q 500 es el limite del sistema

            logger.logOverflow();

            try {

                while (!procesosFinalizados.isEmpty()) {
                    Proceso proceso = procesosFinalizados.pop();
                    logger.logProcesoOverflow(proceso);
                }

            } catch (EmptyStackException e) {
                System.out.println("El stack esta vacio");
            }
        }

        procesoEjecutado.setEstado("FINISHED");
        procesoEjecutado.setFinalizacion("ERROR");
        procesosFinalizados.push(procesoEjecutado);

        logger.logFinishError(procesoEjecutado);

        procesoEjecutado = null;

        System.out.println("El proceso se finalizo con error");

    }

    @Override
    public void terminateProcess(int uid) {
        if (procesosFinalizados.size() == 500) { //Asumimos q 500 es el limite del sistema

            logger.logOverflow();

            try {

                while (!procesosFinalizados.isEmpty()) {
                    Proceso proceso = procesosFinalizados.pop();
                    logger.logProcesoOverflow(proceso);
                }

            } catch (EmptyStackException e) {
                System.out.println("El stack esta vacio");
            }
        }

        procesoEjecutado.setEstado("FINISHED");
        procesoEjecutado.setFinalizacion("ERROR");
        procesosFinalizados.push(procesoEjecutado);

        logger.logFinishTerm(procesoEjecutado, uid);

        procesoEjecutado = null;

        System.out.println("El proceso fue terminado por el usuario: " + uid);
    }

    @Override
    public void printStatus() throws EmptyStackException {
        System.out.println("PROCESS STATUS");
        if (procesoEjecutado != null) {
            System.out.println("EXECUTING:");
            System.out.println("    PID=" + procesoEjecutado.getPid() + " | " + procesoEjecutado.getNombre() + " | USER: " + procesoEjecutado.getUsuario().getTipo() + " UID: " + procesoEjecutado.getUsuario().getUid() + " | P= " + procesoEjecutado.getPrioridad());
        }

        if (!procesosPendientes.isEmpty()) {
            System.out.println("PENDING:");

            MyList<Proceso> auxiliar = new MyLinkedListImpl<>();

            while (!procesosPendientes.isEmpty()) {
                Proceso procesoaux = procesosPendientes.remove();
                auxiliar.add(procesoaux);
                System.out.println("  PID=" + procesoaux.getPid() + " | " + procesoaux.getNombre() + " | USER: " + procesoaux.getUsuario().getTipo() + " UID: " + procesoaux.getUsuario().getUid() + " | P= " + procesoaux.getPrioridad());
            }

            while (!auxiliar.isEmpty()) {
                procesosPendientes.insert(auxiliar.remove(0));
            }
        }

        if (!procesosFinalizados.isEmpty()) {
            System.out.println("FINISHED:");

            MyStack<Proceso> auxiliar = new MyStackImpl<>();

            while (!procesosFinalizados.isEmpty()) {
                Proceso procesoaux = null;
                try {
                    procesoaux = procesosFinalizados.pop();
                } catch (EmptyStackException e) {
                    System.out.println("Esta vacia."); // nunca va a tirar esto
                }

                auxiliar.push(procesoaux);

                assert procesoaux != null;

                System.out.println("  PID=" + procesoaux.getPid() + " " + procesoaux.getNombre() + " | STATE: " + procesoaux.getFinalizacion() + " | USER: " + procesoaux.getUsuario().getTipo() + " UID: " + procesoaux.getUsuario().getUid());
            }

            while (!auxiliar.isEmpty()) {
                procesosFinalizados.push(auxiliar.pop());
            }
        }
    }

    @Override
    public void printStatusVerbose() throws EmptyStackException {
        System.out.println("PROCESS STATUS");

        if (procesoEjecutado != null) {
            System.out.println("EXECUTING:");

            System.out.println("    PID=" + procesoEjecutado.getPid() + " | " + procesoEjecutado.getNombre() + " | USER: " + procesoEjecutado.getUsuario().getTipo() + " UID: " + procesoEjecutado.getUsuario().getUid() + " | P= " + procesoEjecutado.getPrioridad());

            procesoEjecutado.printEventos();
        }

        if (!procesosPendientes.isEmpty()) {
            System.out.println("PENDING:");

            MyList<Proceso> auxiliar = new MyLinkedListImpl<>();

            while (!procesosPendientes.isEmpty()) {
                Proceso procesoaux = procesosPendientes.remove();

                auxiliar.add(procesoaux);

                System.out.println("  PID=" + procesoaux.getPid() + " | " + procesoaux.getNombre() + " | USER: " + procesoaux.getUsuario().getTipo() + " UID: " + procesoaux.getUsuario().getUid() + " | P= " + procesoaux.getPrioridad());

                procesoaux.printEventos();
            }

            while (!auxiliar.isEmpty()) {
                procesosPendientes.insert(auxiliar.remove(0));
            }
        }


        if (!procesosFinalizados.isEmpty()) {
            System.out.println("FINISHED:");

            MyStack<Proceso> auxiliar = new MyStackImpl<>();

            while (!procesosFinalizados.isEmpty()) {
                Proceso procesoaux = null;

                try {
                    procesoaux = procesosFinalizados.pop();
                } catch (EmptyStackException e) {
                    System.out.println("Esta vacia."); // nunca va a tirar esto
                }

                auxiliar.push(procesoaux);

                assert procesoaux != null;

                System.out.println("  PID=" + procesoaux.getPid() + " " + procesoaux.getNombre() + " | STATE: " + procesoaux.getFinalizacion() + " | USER: " + procesoaux.getUsuario().getTipo() + " UID: " + procesoaux.getUsuario().getUid());

                procesoaux.printEventos();
            }

            while (!auxiliar.isEmpty()) {
                procesosFinalizados.push(auxiliar.pop());
            }
        }
    }

    @Override
    public void printStatusByUser(int uid) {
        String uidS = String.valueOf(uid);


        System.out.println("PROCESS STATUS");

        if (procesoEjecutado != null && procesoEjecutado.getUsuario().getUid().equals(uidS)) {
            System.out.println("EXECUTING:");

            System.out.println("    PID=" + procesoEjecutado.getPid() + " | " + procesoEjecutado.getNombre() + " | USER: " + procesoEjecutado.getUsuario().getTipo() + " UID: " + procesoEjecutado.getUsuario().getUid() + " | P= " + procesoEjecutado.getPrioridad());
        }

        if (!procesosPendientes.isEmpty()) {
            System.out.println("PENDING:");

            MyList<Proceso> auxiliar = new MyLinkedListImpl<>();

            while (!procesosPendientes.isEmpty()) {
                Proceso procesoaux = procesosPendientes.remove();

                auxiliar.add(procesoaux);

                if (procesoaux.getUsuario().getUid().equals(uidS)) {
                    System.out.println("  PID=" + procesoaux.getPid() + " | " + procesoaux.getNombre() + " | USER: " + procesoaux.getUsuario().getTipo() + " UID: " + procesoaux.getUsuario().getUid() + " | P= " + procesoaux.getPrioridad());
                }


            }

            while (!auxiliar.isEmpty()) {
                procesosPendientes.insert(auxiliar.remove(0));
            }
        }

        if (!procesosFinalizados.isEmpty()) {
            System.out.println("FINISHED:");

            MyStack<Proceso> auxiliar = new MyStackImpl<>();

            while (!procesosFinalizados.isEmpty()) {
                Proceso procesoaux = null;

                try {
                    procesoaux = procesosFinalizados.pop();
                } catch (EmptyStackException e) {
                    System.out.println("Esta vacia."); // nunca va a tirar esto
                }

                auxiliar.push(procesoaux);

                assert procesoaux != null;

                if (procesoaux.getUsuario().getUid().equals(uidS)) {
                    System.out.println("  PID=" + procesoaux.getPid() + " " + procesoaux.getNombre() + " | STATE: " + procesoaux.getFinalizacion() + " | USER: " + procesoaux.getUsuario().getTipo() + " UID: " + procesoaux.getUsuario().getUid());

                }

            }

            while (!auxiliar.isEmpty()) {
                try {
                    procesosFinalizados.push(auxiliar.pop());
                } catch (EmptyStackException e) {
                    System.out.println("El stack esta vacio");
                }
            }
        }


    }

    @Override
    public void printStatusByProcess(int pid) {
        String pidS = String.valueOf(pid);

        if (procesoEjecutado.getPid().equals(pidS)) {

            System.out.println("Pid: " + procesoEjecutado.getPid() + " Nombre: " + procesoEjecutado.getNombre() + " Estado: RUNNING " + "Usuario: " + procesoEjecutado.getUsuario() + "UID: " + procesoEjecutado.getUsuario().getUid() + "Prioridad: " + procesoEjecutado.getPrioridad());

            procesoEjecutado.printEventos();

        } else{

            boolean found =false;
            MyList<Proceso> auxiliar = new MyLinkedListImpl<>();

            while (!procesosPendientes.isEmpty()) {
                Proceso procesoaux = procesosPendientes.remove();
                auxiliar.add(procesoaux);

                if (procesoaux.getPid().equals(pidS)) {
                    System.out.println("Pid: " + procesoaux.getPid() + " Nombre: " + procesoaux.getNombre() + " Estado: PENDING " + "Usuario: " + procesoaux.getUsuario() + "UID: " + procesoaux.getUsuario().getUid() + "Prioridad: " + procesoaux.getPrioridad());

                    procesoaux.printEventos();

                    found = true;

                    break;
                }
            }

            while (!auxiliar.isEmpty()) {
                procesosPendientes.insert(auxiliar.remove(0));
            }

            if(found==true){
                return;
            }



            if (!procesosFinalizados.isEmpty()) {

                MyStack<Proceso> auxiliar2 = new MyStackImpl<>();

                while (!procesosFinalizados.isEmpty()) {
                    Proceso procesoaux2 = null;

                    try {
                        procesoaux2 = procesosFinalizados.pop();
                    } catch (EmptyStackException e) {
                        System.out.println("Esta vacia."); // nunca va a tirar esto
                    }

                    auxiliar2.push(procesoaux2);

                    if(procesoaux2.getPid().equals(pidS)){
                        System.out.println("Pid: " + procesoaux2.getPid() + " Nombre: " + procesoaux2.getNombre() + " Estado: FINISHED "+procesoaux2.getFinalizacion() + "Usuario: " + procesoaux2.getUsuario() + "UID: " + procesoaux2.getUsuario().getUid() + "Prioridad: " + procesoaux2.getPrioridad());

                        procesoaux2.printEventos();

                        break;
                    }

                    assert procesoaux2 != null;


                }

                while (!auxiliar.isEmpty()) {
                    try {
                        procesosFinalizados.push(auxiliar2.pop());
                    } catch (EmptyStackException e) {
                        System.out.println("El stack esta vacio");
                    }
                }
            }


        }
    }
}
