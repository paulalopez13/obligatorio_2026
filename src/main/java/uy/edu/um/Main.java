package uy.edu.um;

import uy.edu.um.doors.ProcessConsole;
import uy.edu.um.doors.ProcessManagerImpl;

public class Main {
    public static void main(String[] args) {

        ProcessConsole pc = new ProcessConsole(new ProcessManagerImpl());
        pc.init();

    }
}

// PAU pload -p /Users/paulalopez/obligatorio_2026/process.csv -u /Users/paulalopez/obligatorio_2026/users.csv
