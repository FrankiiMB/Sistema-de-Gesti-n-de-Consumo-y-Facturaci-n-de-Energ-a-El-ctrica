package lecturafacturacionyrepartoderecibos;

import altanuevosuministro.OrdenServicio;

public class OrdenLectura extends OrdenServicio {

	private String ruta;
	private String bloque;
	private ContratistaLectura contratistaAsignado;
	private boolean enviada;

	public OrdenLectura() {
	}

	public OrdenLectura(String ruta, String bloque) {
		this.ruta = ruta;
		this.bloque = bloque;
	}

	public void enviarAContratista(ContratistaLectura contratista) {
		if (contratista == null) {
			throw new IllegalArgumentException("Debe asignar un contratista de lectura válido.");
		}
		this.contratistaAsignado = contratista;
		this.enviada = true;
	}

	public String getRuta() { return ruta; }
	public void setRuta(String ruta) { this.ruta = ruta; }
	public String getBloque() { return bloque; }
	public void setBloque(String bloque) { this.bloque = bloque; }
	public ContratistaLectura getContratistaAsignado() { return contratistaAsignado; }
	public boolean isEnviada() { return enviada; }
}