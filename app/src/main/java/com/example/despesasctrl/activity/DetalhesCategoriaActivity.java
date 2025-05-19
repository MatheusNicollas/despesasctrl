package com.example.despesasctrl.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.despesasctrl.R;
import com.example.despesasctrl.adapter.DespesaAdapter;
import com.example.despesasctrl.data.DatabaseHelper;
import com.example.despesasctrl.data.Despesa;
import com.example.despesasctrl.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DetalhesCategoriaActivity extends AppCompatActivity {

    private TextView textCategoria;
    private ListView listViewDetalhes;
    private TextView textTotal;
    private DatabaseHelper dbHelper;
    private String categoriaAtual;
    private String dataInicio;
    private String dataFim;
    private TextView btnVoltar;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_categoria);

        textCategoria = findViewById(R.id.textCategoria);
        listViewDetalhes = findViewById(R.id.listViewDetalhes);
        textTotal = findViewById(R.id.textTotal);
        btnVoltar = findViewById(R.id.btnVoltar);

        btnVoltar.setOnClickListener(v -> {
            finish();
        });

        dbHelper = new DatabaseHelper(this);

        Intent intent = getIntent();
        categoriaAtual = intent.getStringExtra("categoria");
        dataInicio = intent.getStringExtra("data_inicio");
        dataFim = intent.getStringExtra("data_fim");

        String[] partes = dataFim.split("-");

        int ano = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);

        atualizarTituloResumo(dataInicio, dataFim, categoriaAtual);
        carregarDespesas();
    }

    private void atualizarTituloResumo(String dataInicio, String dataFim, String categoria) {

        String dataInicioFormatada = Utils.formatarDataParaExibicao(dataInicio);
        String dataFimFormatada = Utils.formatarDataParaExibicao(dataFim);

        String tituloResumo = String.format("Gastos com %s de %s a %s", categoria, dataInicioFormatada, dataFimFormatada);

        textCategoria.setText(tituloResumo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            carregarDespesas();
        }
    }

    private void carregarDespesas() {
        List<Despesa> despesas = dbHelper.obterDespesasPorCategoriaEData(categoriaAtual, dataInicio, dataFim);

        DespesaAdapter adapter = new DespesaAdapter(
                this,
                despesas,
                dbHelper,
                this::carregarDespesas
        );

        listViewDetalhes.setAdapter(adapter);
        calcularTotal(despesas);
    }

    private void calcularTotal(List<Despesa> despesas) {
        double total = 0.0;
        for (Despesa d : despesas) {
            total += d.getValor();
        }

        String totalFormatado = String.format("%.2f", total).replace(".", ",");
        textTotal.setText("💰 Total gasto: R$ " + totalFormatado);
    }
}
