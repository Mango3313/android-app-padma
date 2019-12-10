package com.example.padmapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.padmapp.R;
import com.example.padmapp.Retrofit.IMyService;
import com.example.padmapp.Retrofit.RetrofitClient;
import com.example.padmapp.clases.TokenPreferences;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONObject;

import java.util.Date;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link pacienteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link pacienteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class pacienteFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    MaterialEditText txtPaciente, txtEstatus,txtProgreso, txtFechaIngreso, txtNumVisitas, txtFechaUltimaVisita,
            txtFechaProximaVisita, txtFechaSalida, txtNombreMedico, txtMedicamentos, txtObservaciones;

    Button btnBuscar;
    private IMyService serv;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private TokenPreferences _token;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public pacienteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment pacienteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static pacienteFragment newInstance(String param1, String param2) {
        pacienteFragment fragment = new pacienteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            //Retrofit client = RetrofitClient.getInstance();
            //serv = client.create(IMyService.class);
        }
    }

    @Override
    public void onClick(View view) {
        String nombre = txtPaciente.getText().toString();
        if (TextUtils.isEmpty(nombre)){
            Toast.makeText(view.getContext(), "Ingresa tu usuario", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("PREFE",_token.getToken());

         compositeDisposable.add(serv.buscarPaciente(nombre,"Bearer "+_token.getToken()).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        JSONObject json  = new JSONObject(s);
                        if(json.getBoolean("error") != true){
                            Log.d("RESPUESTA",s);
                            JSONObject realJSON = json.getJSONObject("message");
                            txtEstatus.setText(realJSON.getString("estado"));
                            txtProgreso.setText(realJSON.getString("progreso"));
                            txtFechaIngreso.setText(new Date(Long.parseLong(realJSON.getString("fecha_ingreso"))).toString());
                            txtNumVisitas.setText(realJSON.getString("num_visitas"));
                            txtFechaUltimaVisita.setText(realJSON.getString("fecha_ultima_visita"));
                            txtFechaProximaVisita.setText(realJSON.getString("fecha_proxima_visita"));
                            txtFechaSalida.setText(realJSON.getString("fecha_aprox_salida"));
                            txtNombreMedico.setText(realJSON.getString("id_doctor"));
                            txtMedicamentos.setText(realJSON.getString("id_med"));
                            txtObservaciones.setText(realJSON.getString("observaciones"));
                        }else{
                            Toast.makeText(getContext(),json.getString("message"),Toast.LENGTH_LONG).show();
                        }

                    }
                }));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_paciente, container, false);

        txtPaciente =  vista.findViewById(R.id.txtPaciente);
        txtEstatus =  vista.findViewById(R.id.txtEstatus);
        txtProgreso=  vista.findViewById(R.id.txtProgreso);
        txtFechaIngreso =  vista.findViewById(R.id.txtFechaIngreso);
        txtNumVisitas =  vista.findViewById(R.id.txtNumVisitas);
        txtFechaUltimaVisita =  vista.findViewById(R.id.txtFechaUltimaVisita);
        txtFechaProximaVisita =  vista.findViewById(R.id.txtFechaProximaVisita);
        txtFechaSalida =  vista.findViewById(R.id.txtFechaSalida);
        txtNombreMedico =  vista.findViewById(R.id.txtNombreMedico);
        txtMedicamentos =  vista.findViewById(R.id.txtMedicamentos);
        txtObservaciones =  vista.findViewById(R.id.txtObservaciones);
        _token = new TokenPreferences(this.getActivity().getApplicationContext());
        Retrofit retrofitClient =  RetrofitClient.getInstance();
        serv = retrofitClient.create(IMyService.class);
        btnBuscar =  vista.findViewById(R.id.btnBuscar);
        btnBuscar.setOnClickListener(this);

        return vista;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
