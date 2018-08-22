package modelo;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileNotFoundException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.function.*;
import org.apache.commons.math3.exception.TooManyIterationsException;
import org.apache.commons.math3.optim.MaxIter;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.linear.LinearConstraint;
import org.apache.commons.math3.optim.linear.LinearConstraintSet;
import org.apache.commons.math3.optim.linear.LinearObjectiveFunction;
import org.apache.commons.math3.optim.linear.NonNegativeConstraint;
import org.apache.commons.math3.optim.linear.Relationship;
import org.apache.commons.math3.optim.linear.SimplexSolver;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;

import com.google.common.collect.Lists;

import exceptions.ExceptionsHandler;
import modelo.devices.DeviceFactory;
import modelo.devices.Dispositivo;
import modelo.devices.DispositivoEstandar;
import modelo.devices.DispositivoInteligente;
import modelo.users.Cliente;
import modelo.users.Cliente.TipoDocumento;

//
	import java.util.HashMap;
	import java.util.Map;
	import java.util.Map.Entry;
	import java.util.*;

@SuppressWarnings("unused")
public class MetodoSimplex {
	
	private SimplexSolver simplex = new SimplexSolver();
	private LinearObjectiveFunction funcionEconomica;
	private Collection<LinearConstraint> restricciones = new ArrayList<LinearConstraint>();
	private GoalType objetivo = GoalType.MAXIMIZE;
	private boolean variablesPositivas = true;
	private List<Double> horasUsoMax = new ArrayList<Double>();
	private List<Double> horasUsoMin = new ArrayList<Double>();
	
	//mari
	//private List<DispositivoInteligente> dispositivos = new ArrayList<DispositivoInteligente>();
	//private List<Dispositivo> dispositivos = new ArrayList<Dispositivo>();
	
	//salo
	private List<Dispositivo> dispositivos = new ArrayList<>();
	
	private double[] listaKWH = new double[dispositivos.size()];
	private List<double[]> posicionCanonica = new ArrayList<double[]>();	
	
	public MetodoSimplex(){}
	
	// --------------------- METODOS DADOS --------------------------------------
	
	public void crearFuncionEconomica(double ... coeficientes) {
		this.funcionEconomica = new LinearObjectiveFunction(coeficientes, 0);
	}
	
	public void agregarRestriccion(Relationship unComparador, double valorAcomparar, double ... coeficientes) {
		this.restricciones.add(new LinearConstraint(coeficientes,unComparador, valorAcomparar));
	}
	
	public PointValuePair resolver() throws TooManyIterationsException{
		return this.simplex.optimize(
										new MaxIter(100),
										this.funcionEconomica,
										new LinearConstraintSet(this.restricciones),
										this.objetivo,
										new NonNegativeConstraint(this.variablesPositivas)
				);
	}
	
// ----------------------Metodos para armar la funcion economica con cantidad variable de argumentos -------------------------------

	public PointValuePair aplicarMetodoSimplex(List<Dispositivo> disp,double consEstandarMes) throws FileNotFoundException, InstantiationException, IllegalAccessException{ 
		dispositivos = disp;
		List<String> tipos = listaDeDescrip();
		Map<String,Integer> cantPorTipoDescrip = countFrequencies(tipos); 
		double[] listaCantidades = arrayCantidades(cantPorTipoDescrip);    
		listaCantidades = revertirArray(listaCantidades);
        crearFuncionEconomica(listaCantidades);
		agregarRestricciones(consEstandarMes);
        return resolver();
	}

	// restricciones = total kwh, los kwh individuales, relacion mayor o menor, y sus posiciones como versor canonico
	// ver test para entender esto
	
	public void agregarRestricciones(double horasARestarEstandar){
			//cargo datos a las listas
			generarArgumentos();
			//genero las restricciones
			agregarRestriccion(Relationship.LEQ, 440640 - horasARestarEstandar, listaKWH); // kwh2.x2 + kwh1.x1 + kwh0.x0 <= 44064
			
			int j =0; int k = 0; int tam = dispositivos.size()*2;
			for(int i=1; i<=tam; i++){
				if(i%2==0){ //par --> lim superior, <=
					agregarRestriccion(Relationship.LEQ,horasUsoMax.get(j),posicionCanonica.get(j));//j j 
					j++;//j++
				} else { //impar --> lim inferior, >=
					agregarRestriccion(Relationship.GEQ,horasUsoMin.get(k),posicionCanonica.get(k));//k k
					k++;//k++
				}
			}
	}
	
	/*
	 * if(i%2==0){  //par --> lim inferior, >=
				agregarRestriccion(Relationship.LEQ,horasUsoMax.get(k),posicionCanonica.get(k)); //GEQ
				k++;
			} else {//impar --> lim superior, <=
				agregarRestriccion(Relationship.GEQ,horasUsoMin.get(j),posicionCanonica.get(j)); //LEQ
				j++;
	 */
	
	//genera argumentos para las agregar restricciones de mayor y menor de kwh, y su versor posicion
	
	public void generarArgumentos(){
		
		//llena la lista de horas uso maximo
		dispositivos.stream().forEach(d -> horasUsoMax.add(d.getHorasUsoMax()));
		
		//llena la lista de horas uso minimo
		dispositivos.stream().forEach(d -> horasUsoMin.add(d.getHorasUsoMin()));
		
		//llena la lista de versores canonicos
		double[] versor = null;
		for(int i=0; i<dispositivos.size(); i++){
			versor = generarVersorCanonica(dispositivos.size(),i);
			posicionCanonica.add(versor);
		}
		//al reves porque lo necesita en este orden: f(x0,x1,x2) = x2 + x1 + x0
		List<double[]> reverseView = Lists.reverse(posicionCanonica); 
		posicionCanonica = reverseView;
		
		double[] listaKWHTemp = new double[dispositivos.size()];		
		for(int i=0; i<dispositivos.size(); i++){
			listaKWHTemp[i] = dispositivos.get(i).getkWh();
		}
		listaKWH = revertirArray(listaKWHTemp);
		
	}

//--------------------------------------------------------
	
	public double[] revertirArray(double[] lista){
		for(int i = 0; i < lista.length / 2; i++) {
		    double temp = lista[i];
		    lista[i] = lista[lista.length - i - 1];
		    lista[lista.length - i - 1] = temp;
		}
		return lista;
	}
	
	public double[] generarVersorCanonica(int cantDisp, int posicion){
		double[] versor = null;
		versor = generarListaCoeficiente(cantDisp,0);
		versor[posicion] = 1;
		return versor;
	}
	
	public double[] generarListaCoeficiente(int cantCoef,int numero){
		double[] coeficientes = new double[cantCoef];
		for(int i=0; i<cantCoef; i++){
			coeficientes[i] = numero;
		}
		return coeficientes;
	}
	
	public double[] convertirListToCoef(List<Integer> list){
		double[] target = new double[list.size()];
		 for (int i = 0; i < target.length; i++) {
		    target[i] = list.get(i).doubleValue();
		 }
		 return target;
	}

//---------------------------------------------------- Otros metodos que use

	public Map<String,Integer> countFrequencies(List<String> tipos) {
 		Map<String,Integer> cantportipo = new HashMap<String,Integer>();
        Set<String> st = new HashSet<String>(tipos);
        for(String tipo : tipos) {
        	if(!cantportipo.containsKey(tipo)) {
        	cantportipo.put(tipo,Collections.frequency(tipos,tipo));
        	}
        }
        return cantportipo;
    }
	
	public List<String> listaDeDescrip(){
		List<String> tiposDescrip = new ArrayList<>();
		for(int i =0;i<dispositivos.size();i++) {
			String unaDescrip = dispositivos.get(i).getEquipoConcreto();
			tiposDescrip.add(unaDescrip);
		}
		return tiposDescrip;
	}
	
	public double[] arrayCantidades(Map<String,Integer> cantPorTipoDesc) {
		double[] arrayCantidades = new double[cantPorTipoDesc.size()];
		int i = 0;
		for(Entry<String,Integer> unValor : cantPorTipoDesc.entrySet()) {
			arrayCantidades[i] = unValor.getValue().doubleValue();
			i++;
		}
		return arrayCantidades;
	}

}