package pe.andessur.sigo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Servidor de la oficina central (San Isidro) del SIGO 4.0.
 *  - TCP 5000: recibe partes diarios en JSON (protocolo propio ENVIAR_PARTE / OK / ERROR).
 *  - UDP 5001: recibe lecturas de temperatura de los sensores de curado.
 */
public class ServidorCentral {

    public static final int PUERTO_TCP = 5000;
    public static final int PUERTO_UDP = 5001;

    public static void main(String[] args) throws Exception {
        Thread hiloUdp = new Thread(ServidorCentral::escucharSensoresUDP, "udp-sensores");
        hiloUdp.setDaemon(true);
        hiloUdp.start();

        try (ServerSocket servidor = new ServerSocket(PUERTO_TCP)) {
            log("Servidor TCP escuchando en el puerto " + PUERTO_TCP);
            log("Servidor UDP escuchando en el puerto " + PUERTO_UDP);
            while (true) {
                Socket cliente = servidor.accept();
                new Thread(() -> atenderCliente(cliente)).start();
            }
        }
    }

    private static void atenderCliente(Socket socket) {
        String origen = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        log("[TCP] Conexion desde " + origen);
        try (socket;
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {

            String linea;
            while ((linea = in.readLine()) != null) {
                log("[TCP] Recibido de " + origen + ": " + linea);
                String respuesta = procesar(linea);
                out.println(respuesta);
                log("[TCP] Respondido a " + origen + ": " + respuesta);
                if (linea.equals("SALIR")) break;
            }
        } catch (Exception e) {
            log("[TCP] Error con " + origen + ": " + e.getMessage());
        }
        log("[TCP] Conexion cerrada " + origen);
    }

    /** Protocolo propio del SIGO 4.0 (una linea por mensaje). */
    static String procesar(String linea) {
        if (linea.startsWith("ENVIAR_PARTE ")) {
            String json = linea.substring("ENVIAR_PARTE ".length()).trim();
            if (json.startsWith("{") && json.endsWith("}") && json.contains("\"obra\"")) {
                return "OK {\"estado\":\"recibido\",\"fecha\":\"" + LocalDateTime.now().withNano(0) + "\"}";
            }
            return "ERROR {\"motivo\":\"JSON invalido o sin campo obra\"}";
        }
        if (linea.equals("PING")) return "PONG";
        if (linea.equals("SALIR")) return "ADIOS";
        return "ERROR {\"motivo\":\"comando desconocido\"}";
    }

    private static void escucharSensoresUDP() {
        try (DatagramSocket socket = new DatagramSocket(PUERTO_UDP)) {
            byte[] buffer = new byte[1024];
            while (true) {
                DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
                socket.receive(paquete);
                String json = new String(paquete.getData(), 0, paquete.getLength(), StandardCharsets.UTF_8);
                log("[UDP] Sensor " + paquete.getAddress().getHostAddress() + ":" + paquete.getPort() + " -> " + json);
            }
        } catch (Exception e) {
            log("[UDP] Error: " + e.getMessage());
        }
    }

    static void log(String msg) {
        System.out.println(LocalDateTime.now().withNano(0) + "  " + msg);
    }
}
