package morosidadcorteyreconexion;


import altanuevosuministro.Suministro;

public class SupervisorCobranzas {

	public boolean analizarMorosidad(Suministro s) {
		return false;
	}

	public OrdenCorte gestionarCorteServicio(Suministro s) {
		return null;
	}

	public boolean verificarPagosAntesDeCorte(OrdenCorte o) {
		return false;
	}

	public OrdenReconexion generarOrdenReconexion(Suministro s) {
		return null;
	}

	public void anularOrdenCorte(OrdenCorte o) {

	}

	public boolean aprobarConvenio(ConvenioPago c) {
		return false;
	}

}
