package ca.pitz.utils;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class ServerUtils {

    public String getServerStatus() throws IOException {
        String[] command = {"sudo", "systemctl", "status", "valheim"};
        Process poc = Runtime.getRuntime().exec(command);
        BufferedReader bf = new BufferedReader(new InputStreamReader(poc.getInputStream()));
        String buffer, output = "";

        while ((buffer = bf.readLine()) != null) {
            output = output.concat(buffer).concat("\n");
        }
        bf.close();

        return output;
    }

    public void restartServer() throws IOException, InterruptedException {
        System.out.println("Restarting server...");
        String[] command = {"sudo", "systemctl", "restart", "valheim"};
        Process poc = Runtime.getRuntime().exec(command);
        poc.waitFor();
        System.out.println("Exit value of process (restart valheim): ".concat(String.valueOf(poc.exitValue())));
    }

    public void updateServerAndRestart() throws IOException, InterruptedException {
        String[] command = {"steamcmd", "+login anonymous", "+force_install_dir /home/x/valheim", "+app_update 896660", "+quit"};
        Process poc = Runtime.getRuntime().exec(command);
        poc.waitFor();
        System.out.println("Exit value of process (steamcmd) : ".concat(String.valueOf(poc.exitValue())));
        this.restartServer();
    }
}
