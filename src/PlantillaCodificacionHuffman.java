import java.util.Map;

import entrada_salida.EscritorBinario;
import entrada_salida.LectorBinario;
import estructuras_datos.ArbolHuffman;

/*********************************************************************************************
 *  Ejecución: 
 *  	% Comprimir:    java PlantillaCodificacionHuffman -c filePathIn filePathOut
 *      % Decomprimir:  java PlantillaCodificacionHuffman -d filePathIn filePathOut
 *  
 *  Utilidad: Permite la compresión/descompresión usando el algoritmo de Huffman
 *  de un archivo de entrada hacia un archivo de salida. 
 *  
 *
 *********************************************************************************************/
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import entrada_salida.EscritorBinario;
import entrada_salida.LectorBinario;
import estructuras_datos.ArbolHuffman;
import java.util.PriorityQueue;
import java.util.Queue;

/*********************************************************************************************
 * Ejecución: % Comprimir: java PlantillaCodificacionHuffman -c filePathIn
 * filePathOut % Decomprimir: java PlantillaCodificacionHuffman -d filePathIn
 * filePathOut
 * 
 * Utilidad: Permite la compresión/descompresión usando el algoritmo de Huffman
 * de un archivo de entrada hacia un archivo de salida.
 * 
 *
 *********************************************************************************************/

public class PlantillaCodificacionHuffman {

	// Constructor
	private PlantillaCodificacionHuffman() {

	}

	/*
	 * Se lee el archivo de entrada (filePathIn, a comprimir) como secuencia de
	 * palabras de 8 bits usando LectorBinario, después se codifica con el algoritmo
	 * de Huffman y el resultado se escribe usando la clase EscritorBinario hacia el
	 * archivo de salida (filePathOut, comprimido).
	 */
	public void comprimir(String filePathIn, String filePathOut) {

		LectorBinario lector = new LectorBinario(filePathIn);
		// Leer archivo de entrada y almacenar en una cadena
		StringBuilder sb = new StringBuilder();
		while (!lector.esVacio()) {
			char b = lector.leerPalabra(); //AQUI DEVUELVE UNA LETRA, QUE SIGNIFICA LO DE LOS 8 Y 16 BITS
			sb.append(b); // OJO! leerPalabra() devuelve una palabra
							// de 8 bits y el tipo char es de 16 bits
		}
		char[] input = sb.toString().toCharArray();

		/******************************* TAREA1.1 ***********************************
		 * 																			 *
		 * Generar tabla de frecuencias (freq) a partir del array de tipo char input.*
		 * 																			 *
		 ****************************************************************************/

		// Version 1.
		// Con array de enteros.
		// PREGUNTAR si hace falta realizar el casting
		//
		// int[] freq = new int[256];
		// int n;
		// for (int i = 0; i < input.length; i++) {
		// n = (int) input[i];
		// freq[n] = freq[n]++;
		// }

		// Version 2.
		// Utilizamos HashTable
		// PREGUNTAR PORQUE HAY QUE PONER CHARACTER E INTEGER Y NO VALE CON CHAR E INT
		//
		Map<Character, Integer> freq = new HashMap<Character,Integer>();
		char c;
		for (int i = 0; i < input.length; i++) {
			c = input[i];
			if (freq.containsKey(c)) {
				// int val = freq.get(c);
				// val= val+1;
				// freq.put(c,val);
				freq.put(c, freq.get(c) + 1);// Revisar que lo haga correctamente, si no, volver a las 3 lineas de
												// codigo
			} else {
				freq.put(c, 1);
			}
		}
			
			// Construir árbol de Huffman.
			ArbolHuffman arbol = construirArbol(freq);
			
			// Construir diccionario de búsqueda -> Pares (símbolo,código).
			// diccionarioCodigos será una estructura de tipo Map, Hashtable, String[], ...,
			// dependiendo de la implementación elegida.
	        // construirCodigos(diccionarioCodigos,arbol,"");
			Map<Character,String> diccionario = new HashMap<Character,String>();
			construirCodigos(diccionario, arbol, "");
			
			// Codificar la trama (char[]input) usando el diccionario de códigos.
	        // codificar(input,diccionarioCodigos,filePathOut,arbol);
			codificar(input,diccionario,filePathOut,arbol);
	}

	/*
	 * Construir arbol de Huffman a partir de la tabla de frecuencias. (Si se ha
	 * usado una estructura Map para albergar la tabla de frecuencias).
	 */
	private ArbolHuffman construirArbol(Map<Character, Integer> freq) {

		/***************************** TAREA1.2 *********************************
		 * Instanciar cola de prioridad (de tipo TreeSet, PriorityQueue o propia)
		 ************************************************************************/
		Queue<ArbolHuffman> cola = new PriorityQueue<ArbolHuffman>();

		/***************************** TAREA1.3 **********************************
		 * Inicializar la cola de prioridad con árboles simples (nodos hoja)
		 * para cada símbolo de la tabla de frecuencias. Usar la estructura de 
		 * datos de tipo arbol binario que se facilita en los recursos de la práctica 
		 * (ArbolHuffman.java).
		 ************************************************************************/
		Iterator it = freq.entrySet().iterator(); //Creamos un iterator para poder recorrer la tabla hash
		Map.Entry entrada;							//Hacemos que cada iteraccion apunte a una entrada de la tabla (entrySet) que tiene la estructura
		while(it.hasNext()){							//(Clave,Valor). Despues, lo guardamos en 'entrada' que sera del tipo Entry de nuestra tabla hash
			entrada = (Map.Entry) it.next();		//Recorremos el iterator que recorre la tabla hash y para cada entrada, cramos un objeto
			cola.add(new ArbolHuffman((char)entrada.getKey(),(int)entrada.getValue(),null,null)); //de tipo arbolhuffman y lo añadimos a la priorityqueue
		}

		/***************************** TAREA1.4 ********************************** 
		 * Construir el arbol de Huffman
		 * final/completo de manera iterativa retirando de la cola de prioridad el par
		 * de nodos con menor frecuencia.
		 ************************************************************************/
		ArbolHuffman arbolFinal=null; //Creamos una instancia de arbolHuffman
		while(cola.size()>1) {			//Mientras que la cola de prioridad tenga mas de 1 elemento, ejecuta el bucle
			ArbolHuffman aux1 = cola.poll();  //Sacamos de la cola los dos nodos ArbolHuffman con menos Frecuencia
			ArbolHuffman aux2 = cola.poll();
			arbolFinal= new ArbolHuffman('\0',aux1.getFrecuencia()+aux2.getFrecuencia(), aux1, aux2); //Creamos un nuevo arbol con la suma de las frecuencias 
																										//anteriores y cuyos hijos son los arboles anteriormente
																										  //extraidos de la cola
			cola.add(arbolFinal); //Volvemos a meter este arbol en la cola de prioridad
		}

		// Sustituir este objeto retornando el árbol de Huffman final
		// construido en la TAREA1.4
		return cola.poll();
	}

	/*
	 * Construir diccionario de búsqueda -> Pares (símbolo,código). (Si se usa una
	 * estructura Map para albergar el diccionario de códigos).
	 */
	private void construirCodigos(Map<Character, String> diccionarioCodigos, ArbolHuffman arbol, String codigoCamino) {

		/***************************** TAREA1.5 ********************************** 
		 * Para hacer la codificación más rápida, construir un diccionario de búsqueda 
		 * (String[], Map, Hashtable, ...) que permita obtener la codificación binaria  
		 * de cada uno de los símbolos. Construir dicho diccionario/tabla requerirá recorrer 
		 * el árbol de Huffman generado en la TAREA1.4. 
		 * Para obtener la máxima calificación en esta tarea la tabla debe construirse 
		 * recorriendo al árbol una sola vez.
		 ************************************************************************/
		if(arbol.esHoja()) {  //Comprobamos si el arbol es hoja, en caso afirmativo, anotamos su key y su camino en el diccionario
			diccionarioCodigos.put(arbol.getSimbolo(), codigoCamino);
		}else { //Si no es hoja, se comprueba si tiene hijo izquiero o derecho
			if(arbol.getIzquierdo()!=null) { //Dependiendo de si es izquiero o derecho, se añade 0 ó 1 al camino y se realiza recursividad pasando como parametros el diccionario
				construirCodigos(diccionarioCodigos, arbol.getIzquierdo(), codigoCamino + '0'); // el arbol hijo y el nuevo camino.
			
			}
			if(arbol.getDerecho()!=null) {
				construirCodigos(diccionarioCodigos, arbol.getDerecho(), codigoCamino + '1');
			}
		}
	}

	/*
	 * Codificar la trama (char[]input) usando el diccionario de códigos y
	 * escribirla en el archivo de salida cuyo path (String filePathOut) se facilita
	 * como argumento. (Si se usa una estructura Map para albergar el diccionario de
	 * códigos).
	 */
	private void codificar(char[] input, Map<Character, String> diccionarioCodigos, String filePathOut,
			ArbolHuffman arbol) {

		EscritorBinario escritor = new EscritorBinario(filePathOut);

		// Serializar árbol de Huffman para recuperarlo posteriormente en la descompresión.
		serializarArbol(arbol, escritor);

		// Escribir también el número de bytes del mensaje original (sin comprimir).
		escritor.escribirEntero(input.length);

		/*********************************** TAREA1.6 **************************************** 
		 * 																					 *
		 * Codificación usando el diccionario de códigos y escritura en el archivo de salida.*
		 * 																					 *
		 ************************************************************************************/
		for(int i=0; i< input.length; i++) {  //Recorremos los simbolos de la trama de entrada a codificar
			String camino = diccionarioCodigos.get(input[i]); //Para cada simbolo, obtenemos su codificación de Huffman gracias al diccionario
			for(int j=0; j<camino.length();j++) { //Recorremos la secuencia de bits almacenada en camino y escribimos la secuencia en el directorio de salida
				boolean valor;
				if(camino.charAt(j)== '1') {
					valor=true;
				}else {
					valor=false;
				}
				escritor.escribirBit(valor);
			}
		}
		escritor.cerrarFlujo();
	}

	/*
	 * Serializar árbol de Huffman para recuperarlo posteriormente en la
	 * descompresión. Se escribe en la parte inicial del archivo de salida.
	 */
	private void serializarArbol(ArbolHuffman arbol, EscritorBinario escritor) {

		if (arbol.esHoja()) {
			escritor.escribirBit(true);
			escritor.escribirPalabra(arbol.getSimbolo()); // Escribir palabra de 8bits
			return;
		}
		escritor.escribirBit(false);
		serializarArbol(arbol.getIzquierdo(), escritor);
		serializarArbol(arbol.getDerecho(), escritor);
	}

	/*
	 * Se lee el archivo de entrada (filePathIn, a descomprimir) como secuencia de
	 * bits usando LectorBinario, después se descodifica usando el árbol final de
	 * Huffman y el resultado se escribe con la clase EscritorBinario en el archivo
	 * de salida (filePathOut, descomprimido).
	 */
	public void descomprimir(String filePathIn, String filePathOut) {

		LectorBinario lector = new LectorBinario(filePathIn);
		EscritorBinario escritor = new EscritorBinario(filePathOut);

		ArbolHuffman arbol = leerArbol(lector);

		// Número de bytes a escribir
		int length = lector.leerEntero();

		/////////////////////// TAREA1.7///////////////////////
		// Decodificar usando el árbol de Huffman.
		for (int i = 0; i < length; i++) {
			ArbolHuffman x = arbol;
			while (!x.esHoja()) {
				boolean bit = lector.leerBit();
				if (bit)
					x = x.getDerecho();
				else
					x = x.getIzquierdo();
			}
			escritor.escribirPalabra(x.getSimbolo());
		}
		//////////////////////////////////////////////////////

		escritor.cerrarFlujo();
	}

	private ArbolHuffman leerArbol(LectorBinario lector) {

		boolean esHoja = lector.leerBit();
		if (esHoja) {
			char simbolo = lector.leerPalabra();
			return new ArbolHuffman(simbolo, -1, null, null);
		} else {
			return new ArbolHuffman('\0', -1, leerArbol(lector), leerArbol(lector));
		}
	}

	public static void main(String[] args) {

		PlantillaCodificacionHuffman huffman = new PlantillaCodificacionHuffman();
		if (args.length == 3) { // Control de argumentos mejorable!!
			switch (args[0]) {
			case "-c":
				huffman.comprimir(args[1], args[2]);
			break;
			case "-d":
				huffman.descomprimir(args[1], args[2]);
			break;
			default:
				System.out.println("Argumento erroneo");
			}
			/**
			 * if (args[0].equals("-c")) { huffman.comprimir(args[1], args[2]); } else if
			 * (args[0].equals("-d")) { huffman.descomprimir(args[1], args[2]);
			 * 
			 * }
			 **/
		} else {
			System.out.println("Faltan argumentos");
		}
	}

}
