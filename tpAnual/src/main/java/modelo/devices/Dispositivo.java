package modelo.devices;

import java.time.LocalDateTime;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import modelo.deviceState.EstadoDispositivo;;

@MappedSuperclass
public abstract class Dispositivo {
	@Id
	@GeneratedValue
	long id;
	protected String nombreDisp;
	public String equipoConcreto = "";
	
	boolean esInteligente = false; //esto es para el json
	
	protected double kWh;
	protected LocalDateTime fechaRegistro;
	protected double horasDeUso = 0;
	protected double horasUsoMax = 0;
	protected double horasUsoMin = 0;
	boolean esBajoConsumo = false;
	
	//Getters y Setters
	
	public String getNombreDisp() {
		return nombreDisp;
	}
	public void setNombreDisp(String nombreDisp) {
		this.nombreDisp = nombreDisp;
	}
	public String getEquipoConcreto(){
		return equipoConcreto;
	}
	public void setEquipoConcreto(String descripcion){
		this.equipoConcreto = descripcion;
	}
	
	public boolean getEsInteligente(){
		return esInteligente;
	}
	public void setEsInteligente(boolean ansSmart){
		this.esInteligente = ansSmart;
	}	
	public double getkWh() {
		return kWh;
	}
	public void setkWh(double kWh) {
		this.kWh = kWh;
	}
	public double getHorasDeUso() {
		return horasDeUso;
	}
	public void setHorasDeUso(double horasDeUso) {
		this.horasDeUso = horasDeUso;
	}
	public LocalDateTime getFechaRegistro() {
		return fechaRegistro;
	}
	public void setFechaRegistro(int y,int m,int d,int hour,int min,int sec) {
		this.fechaRegistro = LocalDateTime.of(y,m,d,hour,min,sec);
	}
	public void setFechaRegistro(LocalDateTime fecha) {
		this.fechaRegistro = fecha;
	}
	public double getHorasUsoMax() {
		return horasUsoMax;
	}
	public void setHorasUsoMax(double horasDeUsoMax) {
		if(getNombreDisp().equals("Heladera")) {
			this.horasUsoMax = -1; // estos disp no cuentan a la hora de analizar el simplex, porque no se pueden apagar
		} else { this.horasUsoMax = horasDeUsoMax; }
		this.horasUsoMax = horasDeUsoMax;
	}
	public double getHorasUsoMin() {
		return horasUsoMin;
	}
	public void setHorasUsoMin(double horasDeUsoMin) {
		if(getNombreDisp().equals("Heladera")) {
			this.horasUsoMin = -1; // estos disp no cuentan a la hora de analizar el simplex, porque no se pueden apagar
		} else { this.horasUsoMin = horasDeUsoMin; }
		this.horasUsoMin = horasDeUsoMin;
	}
	public boolean getEsBajoConsumo(){
		return esBajoConsumo;
	}
	public void setEsBajoConsumo(boolean ansBajoConsumo){
		this.esBajoConsumo = ansBajoConsumo;
	}
	
	//Funcionalidades
	
	public abstract double consumoTotal();
	
	/* Los DI conocen la duracion exacta del tiempo que tienen funcionando, los DE solo
	 * conocen un aproximado de horas de uso diarias y la fecha de registro, por lo que
	 * calculan cuantos dias vienen funcionando aproximadamente y en base a eso sus horas */
	
	public abstract double horasDeUsoTotales();
	
	public abstract EstadoDispositivo getEstadoDisp(); //Solo para que no llore el cantDisp de cliente
	
	//Para los predicates (puede llegar a ser util mas adelante tambien)
	
	public static boolean esInteligente(Dispositivo disp) {
		return disp instanceof DispositivoInteligente;
	}
	
	public static boolean esEstandar(Dispositivo undisp) {
		return undisp instanceof DispositivoEstandar;
	}
	
	public static boolean esConvertido(Dispositivo undisp) {
		return undisp instanceof DispositivoConvertido;
	}
	
	public static boolean esAmbos(Dispositivo undisp) {
		return (esInteligente(undisp)||esConvertido(undisp));
	}
	
}