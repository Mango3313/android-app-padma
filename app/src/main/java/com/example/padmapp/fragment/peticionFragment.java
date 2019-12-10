package com.example.padmapp.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link peticionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link peticionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class  peticionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TokenPreferences _token;


    MaterialEditText nombreRealizador, descripcion, estado,paciente;
    Button btnSolicitar;
    Button btnBuscar,btnLimpiar;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    IMyService serv;

    private OnFragmentInteractionListener mListener;

    public peticionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment peticionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static peticionFragment newInstance(String param1, String param2) {
        peticionFragment fragment = new peticionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _token = new TokenPreferences(this.getActivity().getApplicationContext());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_peticion, container, false);

        nombreRealizador =  vista.findViewById(R.id.txtNombreRealizador);
        descripcion =  vista.findViewById(R.id.txtDescripcion);
        estado =  vista.findViewById(R.id.txtEstado);
        paciente = vista.findViewById(R.id.txtPaciente);
        _token = new TokenPreferences(this.getActivity().getApplicationContext());
        Retrofit retrofitClient =  RetrofitClient.getInstance();
        serv = retrofitClient.create(IMyService.class);

        btnSolicitar =  vista.findViewById(R.id.btnSolicitar);
        btnBuscar = vista.findViewById(R.id.btnBuscar);
        btnLimpiar = vista.findViewById(R.id.btnLimpiar);
        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnSolicitar.setEnabled(true);
                btnSolicitar.setVisibility(View.VISIBLE);
                btnLimpiar.setEnabled(false);
                btnLimpiar.setVisibility(View.GONE);
                nombreRealizador.setText("");
                descripcion.setText("");
                estado.setText("");
            }
        });
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(paciente.getText().toString())){
                    Toast.makeText(getContext(), "Ingresa tu usuario", Toast.LENGTH_SHORT).show();
                    return;
                }
                buscarDatosPaciente(paciente.getText().toString());
            }
        });
        btnSolicitar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(nombreRealizador.getText().toString())){
                    Toast.makeText(getContext(), "Ingresa tu usuario", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(descripcion.getText().toString())){
                    Toast.makeText(getContext(), "Ingresa tu usuario", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(estado.getText().toString())){
                    Toast.makeText(getContext(), "Ingresa tu usuario", Toast.LENGTH_SHORT).show();
                    return;
                }
                registerPetition(nombreRealizador.getText().toString(),descripcion.getText().toString(),estado.getText().toString());

            }
        });

                return vista;
    }

    private void registerPetition(final String nombreRealizadort, final String descripciont, final String estadot) {

        compositeDisposable.add(serv.registerPetition("Bearer "+_token.getToken(),nombreRealizadort, descripciont, estadot)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        JSONObject json = new JSONObject(response);
                        if(json.getBoolean("error") != true){
                         nombreRealizador.setText("");
                         descripcion.setText("");
                         estado.setText("");
                        }
                        Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                }));
    }
    private void buscarDatosPaciente(String nombrePaciente){
        compositeDisposable.add(serv.buscarPeticion("Bearer "+_token.getToken(),nombrePaciente)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String response) throws Exception {
                        JSONObject json = new JSONObject(response);
                        if(json.getBoolean("error") != true){
                            btnSolicitar.setEnabled(false);
                            btnSolicitar.setVisibility(View.GONE);
                            btnLimpiar.setEnabled(true);
                            btnLimpiar.setVisibility(View.VISIBLE);
                            JSONObject realJson = json.getJSONObject("message");
                            nombreRealizador.setText(realJson.getString("nombreRealizador"));
                            descripcion.setText(realJson.getString("descripcion"));
                            estado.setText(realJson.getString("estado"));
                        }else {
                            Toast.makeText(getContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    }
                }));
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
