package com.demo.survey.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.SeekBar
import android.widget.TextView
import com.demo.survey.BaseActivity
import com.demo.survey.R
import com.demo.survey.utils.PageProgressDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_result.*
import org.json.JSONObject

class ResultActivity : BaseActivity() {

    private var questionArray = ArrayList<String>()
    private var questionArrayCount = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        StatusBarUtil.setTranslucentForImageView(this, 20, null)

        centerTitle.text = "ResultSurvey"
        callListAPI()
    }

    private fun callListAPI() {

        val progressDialog = PageProgressDialog.show(this)
        firestoreDatabaseChannelsData.get()
            .addOnSuccessListener { documentSnapshots ->
                if (documentSnapshots != null) {
                    for (i in 0 until documentSnapshots.size()) {
                        val getDocument = documentSnapshots.documents[i]
                        var resource = getDocument.data.toString().replace("=", "\":\"")
                        resource = resource.replace("{", "{\"")
                        resource = resource.replace("}", "\"}")
                        resource = resource.replace(", ", "\", \"")
                        val getResource = JSONObject(resource)

                        val getKey = getResource.keys()

                        while (getKey.hasNext()) {
                            val key = getKey.next()
                            val answer = getResource.getString(key)

                            var pass = false
                            for (q in 0 until questionArray.size) {
                                if (questionArray[q] == "$key$answer") {
                                    val count = questionArrayCount[q]
                                    pass = true
                                    questionArrayCount.add(q, count + 1)
                                }
                            }

                            if (!pass) {
                                questionArray.add("$key$answer")
                                questionArrayCount.add(1)
                            }
                        }

                    }

                    firestoreDatabaseChannels.get()
                        .addOnSuccessListener { documentSnapshots ->
                            questionView.removeAllViews()
                            if (documentSnapshots != null) {
                                for (i in 0 until documentSnapshots.size()) {
                                    val getDocument = documentSnapshots.documents[i]
                                    var resource = getDocument.data.toString().replace("=", "\":\"")
                                    resource = resource.replace("{", "{\"")
                                    resource = resource.replace("}", "\"}")
                                    resource = resource.replace(", ", "\", \"")
                                    val getResource = JSONObject(resource)

                                    if (getResource.has("Question")) {

                                        val Question = getResource.getString("Question")
                                        val A = getResource.getString("A")
                                        val B = getResource.getString("B")
                                        val C = getResource.getString("C")
                                        val D = getResource.getString("D")

                                        val layoutInflater =
                                            getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                                        val view =
                                            layoutInflater.inflate(
                                                R.layout.view_result,
                                                questionView,
                                                false
                                            )

                                        val questionLabel =
                                            view.findViewById(R.id.questionLabel) as TextView

                                        val pollSeekBar1 =
                                            view.findViewById(R.id.pollSeekBar1) as SeekBar
                                        val pollSeekBar2 =
                                            view.findViewById(R.id.pollSeekBar2) as SeekBar
                                        val pollSeekBar3 =
                                            view.findViewById(R.id.pollSeekBar3) as SeekBar
                                        val pollSeekBar4 =
                                            view.findViewById(R.id.pollSeekBar4) as SeekBar

                                        val answer1 = view.findViewById(R.id.answer1) as TextView
                                        val answer2 = view.findViewById(R.id.answer2) as TextView
                                        val answer3 = view.findViewById(R.id.answer3) as TextView
                                        val answer4 = view.findViewById(R.id.answer4) as TextView

                                        questionLabel.text = Question

                                        answer1.text = A
                                        answer2.text = B
                                        answer3.text = C
                                        answer4.text = D

                                        pollSeekBar1.setOnTouchListener { _, _ -> true }
                                        pollSeekBar2.setOnTouchListener { _, _ -> true }
                                        pollSeekBar3.setOnTouchListener { _, _ -> true }
                                        pollSeekBar4.setOnTouchListener { _, _ -> true }

                                        var max1 = 5
                                        var max2 = 5
                                        var max3 = 5
                                        var max4 = 5

                                        for (q in 0 until questionArray.size) {
                                            if (questionArray[q] == "$Question$A") {
                                                val count = questionArrayCount[q]
                                                pollSeekBar1.progress = count

                                                if (max1 < count) max1 = count
                                            }

                                            if (questionArray[q] == "$Question$B") {
                                                val count = questionArrayCount[q]
                                                pollSeekBar2.progress = count

                                                if (max2 < count) max2 = count
                                            }

                                            if (questionArray[q] == "$Question$C") {
                                                val count = questionArrayCount[q]
                                                pollSeekBar3.progress = count

                                                if (max3 < count) max3 = count
                                            }

                                            if (questionArray[q] == "$Question$D") {
                                                val count = questionArrayCount[q]
                                                pollSeekBar4.progress = count

                                                if (max4 < count) max4 = count
                                            }
                                        }

                                        pollSeekBar1.max = max1
                                        pollSeekBar2.max = max2
                                        pollSeekBar3.max = max3
                                        pollSeekBar4.max = max4

                                        questionView.addView(view)
                                    }
                                }
                            }

                            progressDialog?.dismiss()
                        }

                        .addOnFailureListener {
                            showToast(this, it.message.toString())
                        }

                }
            }
    }

    private val firestoreDatabaseChannels by lazy {
        FirebaseFirestore.getInstance().collection("ListQuestions")
    }

    private val firestoreDatabaseChannelsData by lazy {
        FirebaseFirestore.getInstance().collection("Data")
    }
}