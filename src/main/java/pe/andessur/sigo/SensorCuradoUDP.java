package pe.andessur.sigo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Random;

/**
 * Simula un sensor IoT de curado de concreto (obra Los Pinos).
 * Envia la temperatura por UDP cada 3 segundos (en la obra real seria cada 5 min).
 * Uso: SensorCuradoUDP [ip_servidor] [cantidad_lecturas]
 */
public class SensorCuradoUDP {

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "127.0.0.1";
        int lecturas = args.length > 1 ? Integer.parseInt(args[1]) : 10;
        InetAddress destino = InetAddress.getByName(host);
        Random random = new Random();

        try (DatagramSocket socket = new DatagramSocket()) {
            for (int i = 1; i <= lecturas; i++) {
                double temp = 18 + random.nextDouble() * 10; // 18 a 28 grados C
                String json = String.format(java.util.Locale.US,
                        "{\"sensor\":\"LP-S01\",\"obra\":\"Residencial Los Pinos\","
                        + "\"fechaHora\":\"%s\",\"temperatura\":%.1f,\"unidad\":\"C\"}",
                        LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")), temp);
                byte[] datos = json.getBytes(StandardCharsets.UTF_8);
                socket.send(new DatagramPacket(datos, datos.length, destino, ServidorCentral.PUERTO_UDP));
                System.out.println("Enviado (" + i + "/" + lecturas + "): " + json);
                Thread.sleep(3000);
            }
        }
    }
}
