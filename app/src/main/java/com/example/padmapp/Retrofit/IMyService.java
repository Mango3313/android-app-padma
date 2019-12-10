package com.example.padmapp.Retrofit;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface IMyService {

    @POST("usuarios/crear-usuario")
    @FormUrlEncoded
    Observable <String> registerUser (@Field("nombre") String nombre,
                                      @Field("password") String password);

    @POST("usuarios/login/")
    @FormUrlEncoded
    Observable <String> loginUser (@Field("nombre") String nombre,
                                      @Field("password") String password);


    @POST("peticiones/registrar-peticion")
    @FormUrlEncoded
    Observable <String> registerPetition (@Header("Authorization")String authHeader,@Field("nombreRealizador") String nombreRealizador,
                                            @Field("descripcion") String descripcion,
                                            @Field("estado") String estado);
    @GET("peticiones/obtener-peticion/{nombre}")
    Observable <String> buscarPeticion(@Header("Authorization")String authHeader,@Path("nombre") String nombre);

    @GET("paciente/detalles-paciente/{nombre}")
    Observable<String> buscarPaciente(@Path("nombre") String nombre, @Header("Authorization")String authHeader);
}
