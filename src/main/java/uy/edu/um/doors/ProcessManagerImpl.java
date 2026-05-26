package uy.edu.um.doors;

import uy.edu.um.doors.entities.Proceso;
import uy.edu.um.doors.entities.Usuario;

import uy.edu.um.tad.hash.MyHash;
import uy.edu.um.tad.hash.MyHashImpl;
import uy.edu.um.tad.heap.MyHeap;
import uy.edu.um.tad.heap.MyHeapImpl;
import uy.edu.um.tad.queue.MyQueue;
import uy.edu.um.tad.queue.MyQueueImpl;
import uy.edu.um.tad.stack.MyStack;
import uy.edu.um.tad.stack.MyStackImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class ProcessManagerImpl implements ProcessManager{

    //EL DISEÑO DE LA ESTRUCTURA DE ALMACENAMIENTO DEBE IMPLEMENTARSE EN ESTA CLASE EN RELACIÓN CON LAS ENTIDADES QUE DEFINA

    private MyQueue<Proceso> procesosNuevos;
    private MyHeap<Proceso> procesosPendientes;
    private MyStack<Proceso> procesosFinalizados;
    private MyHash<String, Usuario> usuarios;
    //running process


    public ProcessManagerImpl(){
        this.procesosNuevos = new MyQueueImpl<Proceso>();
        this.procesosPendientes = new MyHeapImpl<Proceso>(false);
        this.procesosFinalizados = new MyStackImpl<Proceso>();
        this.usuarios = new MyHashImpl<String,Usuario>();
    }

    public MyQueue<Proceso> getProcesosNuevos() {
        return procesosNuevos;
    }

    public void setProcesosNuevos(MyQueue<Proceso> procesosNuevos) {
        this.procesosNuevos = procesosNuevos;
    }

    public MyHeap<Proceso> getProcesosPendientes() {
        return procesosPendientes;
    }

    public void setProcesosPendientes(MyHeap<Proceso> procesosPendientes) {
        this.procesosPendientes = procesosPendientes;
    }

    public MyStack<Proceso> getProcesosFinalizados() {
        return procesosFinalizados;
    }

    public void setProcesosFinalizados(MyStack<Proceso> procesosFinalizados) {
        this.procesosFinalizados = procesosFinalizados;
    }

    @Override
    public void loadProcessAndUserData(String processCsvPath, String usersCsvPath) {

        try{
            //Cargar Usuarios
            BufferedReader brU = new BufferedReader(new FileReader(usersCsvPath)); // Hace cosas con el archivo para poder leer una linea a la vez

            String lineaU = brU.readLine(); //lee la linea de (uid,alias,tipo) y se prepara para leer la proxima linea
            lineaU=brU.readLine(); //lee la primer linea con datos y se prepara para la proxima

            while (lineaU != null){ //recorro todas las lineas arrancando en la primera

                //Asumimos que el csv (como se ve en el de ejemplo) esta bien hecho
                //preguntar x las dudas

                //Separamos cada atributo
                String atributosU[]=lineaU.split(";",3);

                //Guardamos los atributos en variables
                String uid= atributosU[0];
                String alias= atributosU[1];
                String tipo= atributosU[2];

                //creamos y guardamos en el hash nuestro usuario
                Usuario nuevoUsuario = new Usuario(uid, alias, tipo);
                this.usuarios.put(uid,nuevoUsuario);

                //Leemos la proxima linea
                lineaU= brU.readLine();



                //Cargar procesos
                BufferedReader brP = new BufferedReader(new FileReader(usersCsvPath));

                String lineaP = brP.readLine(); //lee la linea de (uid,alias,tipo) y se prepara para leer la proxima linea
                lineaP=brP.readLine(); //lee la primer linea con datos y se prepara para la proxima

                while (lineaP != null){ //recorro todas las lineas arrancando en la primera

                    //Asumimos que el csv (como se ve en el de ejemplo) no tiene lineas vacias

                    //Separamos cada atributo
                    String atributosP[]=lineaP.split(";",4);

                    //Guardamos los atributos en variables
                    String pid= atributosP[0];
                    String nombre=atributosP[2];

                    String uidP= atributosP[1];
                    Usuario usuarioP = usuarios.get(uidP);


                    String stringEventos= atributosP[3];
                    stringEventos = stringEventos.substring(1, stringEventos.length()-1); //saca el {} al principio y fin


                    //SEGUIR






                    //creamos y guardamos en el hash nuestro usuario
                    Usuario nuevoUsuario = new Usuario(uid, alias, tipo);
                    this.usuarios.put(uid,nuevoUsuario);




            }


        }



        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void prepareProcesses() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void executeNextProcess() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void finishProcessOk() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void finishProcessError() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void terminateProcess(int uid) {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatus() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusVerbose() {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusByUser(int uid) {
        System.out.println("IMPLEMENTAR");
    }

    @Override
    public void printStatusByProcess(int pid) {
        System.out.println("IMPLEMENTAR");
    }
}
