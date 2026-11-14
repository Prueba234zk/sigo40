package pe.andessur.sigo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

/**
 * Cliente TCP de la obra "Residencial Los Pinos" (Chorrillos).
 * Envia el parte diario en JSON al servidor central y muestra la confirmacion.
 * Uso: ClienteObraTCP [ip_servidor]   (por defecto 127.0.0.1)
 */
public class ClienteObraTCP {

    public static void main(String[] args) throws Exception {
        String host = args.length > 0 ? args[0] : "127.0.0.1";

        String parteDiario = "{"
                + "\"obra\":\"Residencial Los Pinos\","
                + "\"sede\":\"Chorrillos\","
                + "\"fecha\":\"" + LocalDate.now() + "\","
                + "\"residente\":\"Ing. Residente\","
                + "\"asistencia\":80,"
                + "\"avance\":\"Vaciado de losa piso 3\","
                + "\"incidencias\":[]"
                + "}";

        try (Socket socket = new Socket(host, ServidorCentral.PUERTO_TCP);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
             BufferedReader in = new BufferedReader(
                     new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))) {

            System.out.println("Conectado a " + host + ":" + ServidorCentral.PUERTO_TCP);

            enviar(out, in, "PING");
            enviar(out, in, "ENVIAR_PARTE " + parteDiario);
            enviar(out, in, "SALIR");
        }
    }

    private static void enviar(PrintWriter out, BufferedReader in, String mensaje) throws Exception {
        System.out.println(">> " + mensaje);
        out.println(mensaje);
        System.out.println("<< " + in.readLine());
    }
}
