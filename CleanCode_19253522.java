package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MesajlasmaActivity extends AppCompatActivity {

    //İlgili sınıflardan global olarak ürettiğimiz nesneler
    private RecyclerView mesajlarListesi;

    MesajlasmaAdapter mesajlasmaAdapter;

    List<KisilerModel> kisilerModelListesi;

    List<MesajlasmaModel> mesajlarModelListesi;

    DatabaseReference firebaseVeriTabani;


    //init() metodu ile XML'deki nesnelere Java ile ulaştık
    public void init(){
        mesajlarListesi = findViewById(R.id.mesajlarListesi);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_six);

        init();

        mesajlarModelListesi = new ArrayList<>();

        //Mesajları Firebase'de kaydedeceğimiz yerin yolunu belirttik.
        firebaseVeriTabani = FirebaseDatabase.getInstance().getReference()
                .child("Chatlist").child(uId);

        firebaseVeriTabani.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                
                mesajlarModelListesi.clear();

                for (DataSnapshot ds: snapshot.getChildren()){

                    //Mesajları RecyclerView içine ekleyoruz ve kaydediyor.
                    MesajlasmaAdapter firebasedenGelenMesajlar = ds.getValue(MesajlasmaAdapter.class);
                    mesajlarModelListesi.add(firebasedenGelenMesajlar);

                }
                
                mesajlariYukle();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void mesajlariYukle() {

        kisilerModelListesi = new ArrayList<>();

        //Mesajları RecyclerView'de sıralamak için Firebase'den okuma yapmalıyız.
        firebaseVeriTabani = FirebaseDatabase.getInstance().getReference().child("Users");

        firebaseVeriTabani.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                kisilerModelListesi.clear();

                for (DataSnapshot ds: snapshot.getChildren()){

                    KisilerModel firebasedeKayitliKullanicilar = ds.getValue(KisilerModel.class);

                    for (MesajlasmaModel kisilerModelListesi : mesajlarModelListesi){

                        //Firebase'de kayıtlı kullanıcı olup olmadığını ve Firebase ile mesaj id'lerine göre ArrayList'e veri ekliyoruz.
                        if (firebasedeKayitliKullanicilar.getUID() != null && firebasedeKayitliKullanicilar.getUID().equals(firebasedenGelenMesajlar.getId())){
                            kisilerModelListesi.add(firebasedeKayitliKullanicilar);
                            break;
                        }
                    }

                    //MesajlasmaAdapter'daki adapter'ın constuctor'una gerekli olan parametreli veriyoruz.
                    mesajlasmaAdapter = new MesajlasmaAdapter(MesajlasmaActivity.this, kisilerModelListesi);
                    mesajlarListesi.setHasFixedSize(true);
                    //RecyclerView'e ilgili adapter'ımızı ekliyoruz.
                    mesajlarListesi.setAdapter(mesajlasmaAdapter);
                    mesajlarListesi.setLayoutManager(new LinearLayoutManager(MesajlasmaActivity.this));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}