package com.example.trashmorph

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class RecycledArtActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var datalist: ArrayList<Dataclass>
    lateinit var imageList: Array<Int>
    lateinit var titleList:Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recycled_art)

        imageList = arrayOf(
            R.drawable.pots,
            R.drawable.mugs,
            R.drawable.container,
            R.drawable.tiffin,
            R.drawable.papercups,
            R.drawable.utensils
        )

        titleList = arrayOf(
            "Pots:\n" +
                    "₹100+100 points"

                    ,
            "Mugs: \n"
                    + "₹100+100 points",
            "Container: \n" +
                    "₹100+100 points ",
            "Tiffin: \n" +
                    "₹100+100 points",
            "PaperCups: \n" +
                    "₹100+100 points",
            "Utensils: \n" +
                    "₹100+100 points"
        )
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)
        datalist = arrayListOf<Dataclass>()
        getData()



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    private fun getData(){
        for(i in imageList.indices){
            val dataClass=Dataclass(imageList[i],titleList[i])
            datalist.add(dataClass)
        }
        recyclerView.adapter=AdapterClass(datalist)
    }
}
