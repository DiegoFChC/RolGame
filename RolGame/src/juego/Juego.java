package juego;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

import control.Teclado;

public class Juego extends Canvas implements Runnable {

	// Identificador por defecto de serie.
	// Por si en un momento volvemos a usar la clase, java sabe si es la misma o es
	// otra
	private static final long serialVersionUID = 1L;

	private static final int ANCHO = 800; // Final-> constante
	private static final int ALTO = 600;

	private static volatile boolean enFuncionamiento = false; // Si se esta ejecutando el juego
																// Volatile: no se puede usar la variable en dos threads
																// al tiempo

	private static final String NOMBRE = "Juego";

	private static int aps = 0;
	private static int fps = 0;

	private static JFrame ventana; // static-> solo hay un tipo de este
	private static Thread thread; // Implementamos hilos
	private static Teclado teclado;

	// Constructor
	private Juego() {
		// Medidas de la ventana
		setPreferredSize(new Dimension(ANCHO, ALTO));

		teclado = new Teclado();
		addKeyListener(teclado);

		// Iniciamos la ventana y le ponemos nombre
		ventana = new JFrame(NOMBRE);
		// Aseguramos que la x cierre todo el programa
		ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// No se podra cambiar el tamaño de la ventana
		ventana.setResizable(false);
		// Organizador de la ventana
		ventana.setLayout(new BorderLayout());
		// Como extendemos de canvas, iniciamos el canvas
		ventana.add(this, BorderLayout.CENTER);
		// Los componentes se ajustan
		ventana.pack();
		// Posicion de la ventana
		ventana.setLocationRelativeTo(null);
		// Hacemos la ventana visible
		ventana.setVisible(true);
	}

	// Metodo principal
	public static void main(String[] args) {
		Juego juego = new Juego();
		// Llamamos el thread
		juego.iniciar();
	}

	// Iniciamos el segundo thread
	// synchronized: Como valatile
	private synchronized void iniciar() {
		enFuncionamiento = true;

		// creamos el thread
		// Parametros: (ventana sobre la que se ejecuta, nombre del tread)
		thread = new Thread(this, "Graficos");
		// inicia el thread
		thread.start();
	}

	private synchronized void detener() {
		enFuncionamiento = false;

		// Paramos el thread
		try {
			// .stop(), no recomendable, cierra el thread de forma abrupta
			// .join(), espera a que el thread acabe de ejecutar su tarea, y lo cierra
			thread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	// Actualizar los datos del juego
	private void actualizar() {
		teclado.actualizar();

		if (teclado.arriba) {
			System.out.println("arriba");
		}
		if (teclado.abajo) {
			System.out.println("abajo");
		}
		if (teclado.izquierda) {
			System.out.println("izquierda");
		}
		if (teclado.derecha) {
			System.out.println("derecha");
		}

		aps++;
	}

	// Redibujar graficos
	public void mostrar() {
		fps++;
	}

	public void run() {
		// Cambiamos la velocidad a la que se ejeuta el bucle
		// Temporizador
		// NS: Nanosegundos
		// APS: Actualizacion por segundo
		// Nanosegundos en un segundo
		final int NS_POR_SEGUNDO = 1000000000;
		// Actualizaciones por segundo
		final byte APS_OBJETIVO = 60;
		// Nanosegundos por actualizacion
		// Cuantos nanosegundos deben pasar para actualizar 60 veces por segundo
		final double NS_POR_ACTUALIZACION = NS_POR_SEGUNDO / APS_OBJETIVO;

		// Mide el tiempo en nanosegundos
		// System.nanoTime();
		long referenciaActualizacion = System.nanoTime();
		long referenciaContador = System.nanoTime();

		double tiempoTranscurrido;
		// Cantidad de tiempo transcurrido hasta una actualizacion
		double delta = 0;

		// Ponemos en prioridad el foco del canvas
		requestFocus();

		// Lo que este en este bucle se ejecutara mientras el juego este en
		// funcionamiento
		while (enFuncionamiento) {
			// Referencia del tiempo
			final long inicioBucle = System.nanoTime();

			// Cuanto tiempo ha pasado
			tiempoTranscurrido = inicioBucle - referenciaActualizacion;
			referenciaActualizacion = inicioBucle;

			delta += tiempoTranscurrido / NS_POR_ACTUALIZACION;

			while (delta >= 1) {
				// Ejecutara estas funiones mientras el juego este activo
				actualizar();
				delta--;
			}

			// Ejecutara estas funiones mientras el juego este activo
			mostrar();
			// Contador: Se actuaice cada segundo
			if (System.nanoTime() - referenciaContador > NS_POR_SEGUNDO) {
				// Lo escribimos en la cabecera de la ventana
				ventana.setTitle(String.format("%s || APS: %d || FPS: %d", NOMBRE, aps, fps));
				aps = 0;
				fps = 0;
				referenciaContador = System.nanoTime();
			}

		}
	}

}
