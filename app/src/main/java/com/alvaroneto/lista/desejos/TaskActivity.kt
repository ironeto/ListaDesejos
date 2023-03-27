package com.alvaroneto.lista.desejos

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.alvaroneto.lista.desejos.databinding.ActivityTaskBinding
import com.alvaroneto.lista.desejos.services.NotificationReceiver
import java.util.*

class TaskActivity : AppCompatActivity() {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val db_ref = FirebaseDatabase.getInstance().getReference("/users/$uid/tasks")

    var taskId: String = ""

    private lateinit var binding: ActivityTaskBinding;
    private lateinit var firebaseAnalytics: FirebaseAnalytics;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadTask()

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val in_date = findViewById<EditText>(R.id.in_date)
        val in_time = findViewById<EditText>(R.id.in_time)

        val current_date_time = Calendar.getInstance()
        val day = current_date_time.get(Calendar.DAY_OF_MONTH)
        val month = current_date_time.get(Calendar.MONTH)
        val year = current_date_time.get(Calendar.YEAR)
        val hour = current_date_time.get(Calendar.HOUR_OF_DAY)
        val minute = current_date_time.get(Calendar.MINUTE)

        in_date.setText(String.format("%02d/%02d/%04d", day, month + 1, year))
        in_time.setText(String.format("%02d:%02d", hour, minute))

        findViewById<Button>(R.id.btn_date).setOnClickListener{
            val datePickerDialog = DatePickerDialog(this, {_, yearOfYear, monthOfYear, dayOfMonth ->
                in_date.setText(String.format("%02d/%02d/%04d", dayOfMonth, monthOfYear + 1, yearOfYear))
            }, year, month, day)
            datePickerDialog.show()
        }

        findViewById<Button>(R.id.btn_time).setOnClickListener{
            val timePickerDialog = TimePickerDialog(this, {_, hourOfDay, minuteOfHour ->
                in_time.setText(String.format("%02d:%02d", hourOfDay, minuteOfHour))
            }, hour, minute, true)
            timePickerDialog.show()
        }

        findViewById<Button>(R.id.btn_save_task).setOnClickListener{
            createUpdateTask()
        }

        createNotificationChannel()
    }

    private fun createNotificationChannel(){
        val name = "Notification Channel WishList"
        val descriptionText = "Channel for WishList notifications"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel("WihList", name, importance).apply {
            description = descriptionText
        }

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun loadTask(){
        this.taskId = intent.getStringExtra("id") ?: ""
        if(taskId === "") return

        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid/tasks/$taskId")

        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(!snapshot.exists()) return

                findViewById<EditText>(R.id.titulo).setText(snapshot.child("titulo").value.toString())
                findViewById<EditText>(R.id.descricao).setText(snapshot.child("descricao").value.toString())
                findViewById<EditText>(R.id.in_date).setText(snapshot.child("data").value.toString())
                findViewById<EditText>(R.id.in_time).setText(snapshot.child("hora").value.toString())
            }


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TaskActivity, "Erro ao carregar item", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun scheduleNotification(title: String, data: String, hora: String){
        val intent = Intent(this, NotificationReceiver::class.java)
        intent.putExtra("title", title)


        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance()
//        calendar.add(Calendar.MINUTE, 1)
        val dataDia = data.substring(0, 2).toInt()
        val dataMes = data.substring(3, 5).toInt() - 1
        val dataAno = data.substring(6, 10).toInt()
        val horaHora = hora.substring(0, 2).toInt()
        val horaMinuto = hora.substring(3, 5).toInt()

        calendar.set(
            dataAno,
            dataMes,
            dataDia,
            horaHora,
            horaMinuto,
            0
        )
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){ //Verificar a versão da build, pq para cada versão, temos uma forma correta de criar o alarme
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }

        Toast.makeText(this, "Notificação agendada", Toast.LENGTH_SHORT).show()
    }

    fun createUpdateTask(){

        if (findViewById<EditText>(R.id.titulo).text.toString().trim().isEmpty()){
            Toast.makeText(this, "Por favor, informe ao menos o Título", Toast.LENGTH_SHORT).show()
            return;
        }

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, uid)
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "create_task")
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle)

        if(taskId !== ""){
            val ref = FirebaseDatabase.getInstance().getReference("/users/$uid/tasks/$taskId")

            ref.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(!snapshot.exists()) return
                    val task = snapshot.value as HashMap<String, String>

                    task["titulo"] = findViewById<EditText>(R.id.titulo).text.toString()
                    task["descricao"] = findViewById<EditText>(R.id.descricao).text.toString()
                    task["data"] = findViewById<EditText>(R.id.in_date).text.toString()
                    task["hora"] = findViewById<EditText>(R.id.in_time).text.toString()

                    ref.setValue(task)
                    Toast.makeText(this@TaskActivity, "Item atualizado com sucesso", Toast.LENGTH_SHORT).show()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@TaskActivity, "Erro ao atualizar item", Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            val titulo = findViewById<EditText>(R.id.titulo)
            val descricao = findViewById<EditText>(R.id.titulo)
            val data = findViewById<EditText>(R.id.in_date)
            val hora = findViewById<EditText>(R.id.in_time)

            val task =  hashMapOf(
                "titulo" to titulo.text.toString(),
                "descricao" to descricao.text.toString(),
                "data" to data.text.toString(),
                "hora" to hora.text.toString(),
            )

            val novoElemento = db_ref.push()
            novoElemento.setValue(task)

            Toast.makeText(this, "Item criado com sucesso!", Toast.LENGTH_SHORT).show()

            Intent(this, MainActivity::class.java).also {
                startActivity(it)
            }
        }
        scheduleNotification(findViewById<EditText>(R.id.titulo).text.toString(),findViewById<EditText>(R.id.in_date).text.toString(), findViewById<EditText>(R.id.in_time).text.toString())
    }
}