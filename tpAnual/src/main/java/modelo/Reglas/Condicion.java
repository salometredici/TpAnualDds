package modelo.Reglas;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.commons.lang3.EnumUtils;

import exceptions.CaracterInvalidoException;
@Entity
public abstract class Condicion {
	
	//protected Regla regla;
	@Id @GeneratedValue
	protected String comparacion; //Mayor, Menor, Igual o distinto
	protected enum CriterioComparacion {MAYOR,MENOR,IGUAL,DISTINTO}
	protected boolean estado = false;
	
	public boolean evaluar(double x, double y){
		
		switch (this.comparacion){
		case "MAYOR":
			return this.comparacionMayor(x,y);
		case "MENOR":
			return this.comparacionMenor(x,y);
		case "IGUAL":
			return this.comparacionIgual(x,y);
		case "DISTINTO":
			return this.comparaciondesigual(x,y);
		default: return false;
		}
	}

	
	public boolean comparacionMayor(double x, double y){
		if (x>y){
			return true;
		}else 
			return false;
	}
	
	public boolean comparacionMenor(double x, double y){
		if (x<y){
			return true;
		}else 
			return false;
	}
	
	public boolean comparacionIgual(double x, double y){
		if (x==y){
			return true;
		}else 
			return false;
	}
	public boolean comparaciondesigual(double x, double y){
		if (x!=y){
			return true;
		}else 
			return false;
	}
	
	public void setComparacion(String comparacion) throws CaracterInvalidoException{
		this.comparacion = comparacion;
		EnumUtils.isValidEnum(CriterioComparacion.class, comparacion);	
	}

	public abstract void update();

	public abstract boolean getEstado();
	
	public String getComparacion(){
		return this.comparacion;
	}
	
	public abstract String getExpresion();
}
