package ca.pitz.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Slf4j
@Component
public class ServerUtils {

    private final String[] serverExists = {"sudo", "systemctl", "list-units", "|", "grep", "\"valheim\""};
    private final String[] serverStatus = {"sudo", "systemctl", "status", "valheim"};
    private final String[] serverRestart = {"sudo", "systemctl", "restart", "valheim"};
    private final String[] serverUpdate = {"steamcmd", "+login anonymous", "+force_install_dir /home/x/valheim", "+app_update 896660", "+quit"};

    public boolean serverExists() {
        try {
            return !executeProcess(serverExists).isEmpty();
        } catch (IOException e) {
            return false;
        }
    }

    public String getServerStatus() throws IOException {
        return executeProcess(serverStatus);
    }

    public void restartServer() throws IOException, InterruptedException {
        log.info("Restarting server...");
        Process poc = Runtime.getRuntime().exec(serverRestart);
        poc.waitFor();
        log.info("Exit value of process (restart valheim): ".concat(String.valueOf(poc.exitValue())));
    }

    public void updateServerAndRestart() throws IOException, InterruptedException {
        Process poc = Runtime.getRuntime().exec(serverUpdate);
        poc.waitFor();
        log.info("Exit value of process (steamcmd) : ".concat(String.valueOf(poc.exitValue())));
        this.restartServer();
    }

    private String executeProcess(String[] command) throws IOException {
        Process poc = Runtime.getRuntime().exec(command);
        BufferedReader bf = new BufferedReader(new InputStreamReader(poc.getInputStream()));
        String buffer, output = "";

        while ((buffer = bf.readLine()) != null) {
            output = output.concat(buffer).concat("\n");
        }
        bf.close();

        return output;
    }

}
