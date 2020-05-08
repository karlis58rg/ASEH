package mx.hgo.aseh.ui.tools;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ToolsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ToolsViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("VII. Vehículos particulares en los que se trasladen personas que realicen actividades esenciales, así determinadas por las autoridades sanitarias competentes . . . \n" +
                "Financieros (eje. Bancos). \n" +
                "Recaudación tributaria (SAT, SFPH).\n" +
                "Distribución y venta de energéticos (Eje. Oxifuel).\n" +
                "Gasolineras y gas.\n" +
                "Generación y distribución de agua potable (eje. pipas).\n" +
                "Industria de alimentos y bebidas no alcohólicas. \n" +
                "Mercados de alimentos.\n" +
                "Supermercados.\n" +
                "Tiendas de autoservicio. \n" +
                "Abarrotes \n" +
                "Venta de alimentos preparados (eje. Pastes).\n" +
                "Producción agrícola (eje, tractores y maquinaria de siembra).\n" +
                "Pesquera y pecuaria (traslado de animales aptos para consumo).\n" +
                "Industria química (eje. \n" +
                "Productos de limpieza.\n" +
                "Ferreterías.\n" +
                "Servicios de mensajería.\n" +
                "Guardias en labores de seguridad privada. \n" +
                "Guarderías y estancias infantiles.\n" +
                "Asilos y estancias para personas adultas mayores.\n" +
                "Refugios y centros de atención a mujeres víctimas de violencia, sus hijas e hijos.\n" +
                "Telecomunicaciones y medios de información (Telmex)\n" +
                "Soporte de Herramientas de Seguridad Pública (eje. Seguritech) \n" +
                "Servicios privados de emergencia (ambulancias privadas). \n" +
                "Servicios funerarios y de inhumación. \n" +
                "Servicios de almacenamiento de insumos esenciales.");
    }

    public LiveData<String> getText() {
        return mText;
    }
}