package modelo.Reglas;

import modelo.devices.Sensor;

public class CondicionDosSensores extends Condicion{

	private Sensor sensor1;
	private Sensor sensor2;
	
	public CondicionDosSensores(Sensor s1, Sensor s2, String comp){
		this.sensor1 = s1;
		this.sensor2 = s2;
		this.comparacion = comp;
		s1.subscribir(this);
		s2.subscribir(this);
	}
	
	//llamado por sensor
	@Override
	public void update(){
		this.estado = evaluar(sensor1.getMagnitud(), sensor2.getMagnitud());	
		System.out.println("Se evalu�� si: ");
		System.out.println("\nEl sensor 1, " + sensor1.getNombreMagnitud() + " = " + sensor1.getMagnitud());
		System.out.println(" es: " + comparacion + " al ");
		System.out.println(" sensor 2, " + sensor2.getNombreMagnitud() + " = " + sensor2.getMagnitud());
		System.out.println("\nCon resultado: " + estado);
	}
	
	public String getExpresion(){
		return sensor1.getNombreMagnitud().concat(comparacion).concat(sensor2.getNombreMagnitud());
	}
	
	//CUANDO ALGUIEN DESDE AFUERA DESEA AVALUAR LAS CONDICIONES, 
	//Y NO SER DISPARADO AUTOMATICAMENTE POR SENSORES
	//ESTO ESTA PARA CONSULTARLE AL SENSOR EL VALOR ACTUAL DE LA MAGNITUD QUE NECESITO
	@Override
	public boolean getEstado(){
		sensor1.medir();
		sensor2.medir();
		return this.estado;
	}
}
