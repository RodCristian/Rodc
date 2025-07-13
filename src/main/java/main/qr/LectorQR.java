package main.qr;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import main.service.AsistenciaService;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class LectorQR {

    private volatile boolean ejecutando = true;

    public void iniciarLector() {
        List<Webcam> camarasDisponibles = Webcam.getWebcams();

        if (camarasDisponibles.isEmpty()) {
            JOptionPane.showMessageDialog(null, "❌ No se detectaron cámaras.");
            return;
        }

        String[] nombres = camarasDisponibles.stream()
                .map(Webcam::getName)
                .toArray(String[]::new);

        String seleccion = (String) JOptionPane.showInputDialog(
                null,
                "Selecciona la cámara a usar:",
                "Cámara",
                JOptionPane.QUESTION_MESSAGE,
                null,
                nombres,
                nombres[0]);

        if (seleccion == null) {
            System.out.println("ℹ️ Selección de cámara cancelada.");
            return;
        }

        Webcam camara = camarasDisponibles.stream()
                .filter(c -> c.getName().equals(seleccion))
                .findFirst()
                .orElse(null);

        if (camara == null) {
            System.out.println("❌ No se pudo abrir la cámara seleccionada.");
            return;
        }

        camara.setViewSize(new java.awt.Dimension(640, 480));
        WebcamPanel panel = new WebcamPanel(camara);
        panel.setMirrored(true);

        JFrame ventana = new JFrame("Lector de QR - Asistencia");
        ventana.add(panel);
        ventana.setResizable(false);
        ventana.pack();
        ventana.setVisible(true);
        ventana.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ventana.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                ejecutando = false; // Detener el hilo
                if (camara.isOpen()) {
                    camara.close();
                    System.out.println("🔴 Cámara cerrada correctamente.");
                }
            }
        });

        new Thread(() -> {
            while (ejecutando) {
                try {
                    BufferedImage imagen = camara.getImage();
                    if (imagen == null)
                        continue;

                    LuminanceSource fuente = new BufferedImageLuminanceSource(imagen);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(fuente));

                    Result resultado = new MultiFormatReader().decode(bitmap);
                    if (resultado != null) {
                        String rutEscaneado = resultado.getText();
                        System.out.println("QR Detectado: " + rutEscaneado);
                        marcarAsistencia(rutEscaneado);
                        Thread.sleep(3000);
                    }

                } catch (NotFoundException e) {
                    // No se encontró QR en el frame
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void marcarAsistencia(String rut) {
        LocalDate hoy = LocalDate.now();
        String ultimoTipoHoy = AsistenciaService.obtenerUltimoTipoHoy(rut, hoy);

        String siguienteTipo;

        if (ultimoTipoHoy == null) {
            siguienteTipo = "Entrada1";
        } else {
            switch (ultimoTipoHoy) {
                case "Entrada1":
                    siguienteTipo = "Salida Almorzar";
                    break;
                case "Salida Almorzar":
                    siguienteTipo = "Entrada2";
                    break;
                case "Entrada2":
                    siguienteTipo = "Salida";
                    break;
                case "Salida":
                    System.out.println("ℹ️ Jornada completada hoy para RUT: " + rut);
                    return; // No registra más
                default:
                    siguienteTipo = "Entrada1";
                    break;
            }
        }

        AsistenciaService.registrarAsistencia(rut, siguienteTipo);
        System.out.println(
                "✅ Se registró " + siguienteTipo + " para RUT: " + rut + " a las " + LocalTime.now().withNano(0));
    }

}
