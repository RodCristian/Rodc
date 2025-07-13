package main.qr;

import com.github.sarxos.webcam.Webcam;

public class ListaCamaras {
    public static void main(String[] args) {
        for (Webcam cam : Webcam.getWebcams()) {
            System.out.println("🟢 Cámara detectada: " + cam.getName());
        }
    }
}