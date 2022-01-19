package com.demo.survey.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.demo.survey.BaseActivity
import com.demo.survey.R
import com.demo.survey.utils.PageProgressDialog
import com.google.firebase.firestore.FirebaseFirestore
import com.jaeger.library.StatusBarUtil
import kotlinx.android.synthetic.main.activity_question.*
import org.json.JSONObject

class QuestionActivity : BaseActivity() {

    private var answerObject = JSONObject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
        StatusBarUtil.setTranslucentForImageView(this, 20, null)

        centerTitle.text = "SurveyDemo"
        callListAPI()

        submitButton.setOnClickListener {
            var pass = true
            val getKey = answerObject.keys()
            val newData: MutableMap<String, String> = mutableMapOf()

            while (getKey.hasNext()) {
                val key = getKey.next()
                val answer = answerObject.getString(key)
                if (answer == "") pass = false
                newData[key] = answer
            }

            if (pass) {
                firestoreDatabaseChannelsData
                    .add(newData)
                    .addOnSuccessListener {

                        showToast(this, "Great! Your data was sent successfully. Thanks")
                        val i = Intent(this, ResultActivity::class.java)
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(i)
                        finish()
                    }

            } else showToast(this, "Please fill in the information")
        }
    }

    private fun callListAPI() {

        val progressDialog = PageProgressDialog.show(this)
        firestoreDatabaseChannels.get()
            .addOnSuccessListener { documentSnapshots ->
                questionView.removeAllViews()
                answerObject = JSONObject()
                if (documentSnapshots != null) {
                    for (i in 0 until documentSnapshots.size()) {
                        val getDocument = documentSnapshots.documents[i]
                        var resource = getDocument.data.toString().replace("=", "\":\"")
                        resource = resource.replace("{", "{\"")
                        resource = resource.replace("}", "\"}")
                        resource = resource.replace(", ", "\", \"")
                        val getResource = JSONObject(resource)

                        if (getResource.has("1. label")) {
                            val getKey = getResource.keys()

                            while (getKey.hasNext()) {
                                val key = getKey.next()
                                val question = getResource.getString(key)

                                val layoutInflater =
                                    getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                                val view = layoutInflater.inflate(
                                    R.layout.view_question_edit,
                                    questionView,
                                    false
                                )

                                val questionLabel =
                                    view.findViewById(R.id.questionLabel) as TextView
                                val questionEditText =
                                    view.findViewById(R.id.questionEditText) as EditText

                                questionLabel.text = question
                                questionEditText.hint = question
                                if (key == "1. label") {
                                    questionLabel.visibility = View.VISIBLE
                                    questionEditText.visibility = View.GONE
                                } else {
                                    questionLabel.visibility = View.GONE
                                    questionEditText.visibility = View.VISIBLE
                                    answerObject.put("Basic1 $key", "")

                                    questionEditText.addTextChangedListener(object : TextWatcher {

                                        override fun afterTextChanged(searchString: Editable) {}
                                        override fun beforeTextChanged(
                                            s: CharSequence,
                                            start: Int,
                                            count: Int,
                                            after: Int
                                        ) {
                                        }

                                        override fun onTextChanged(
                                            searchString: CharSequence,
                                            start: Int,
                                            before: Int,
                                            count: Int
                                        ) {
                                            val string = searchString.toString()
                                            answerObject.put("Basic1 $key", string)
                                        }
                                    })
                                }

                                questionView.addView(view)
                            }
                        }

                        if (getResource.has("Question")) {

                            val Question = getResource.getString("Question")
                            val A = getResource.getString("A")
                            val B = getResource.getString("B")
                            val C = getResource.getString("C")
                            val D = getResource.getString("D")

                            val layoutInflater =
                                getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                            val view =
                                layoutInflater.inflate(R.layout.view_question, questionView, false)

                            val questionLabel = view.findViewById(R.id.questionLabel) as TextView

                            val answerView1 = view.findViewById(R.id.answerView1) as LinearLayout
                            val answerView2 = view.findViewById(R.id.answerView2) as LinearLayout
                            val answerView3 = view.findViewById(R.id.answerView3) as LinearLayout
                            val answerView4 = view.findViewById(R.id.answerView4) as LinearLayout

                            val answerTick1 = view.findViewById(R.id.answerTick1) as ImageView
                            val answerTick2 = view.findViewById(R.id.answerTick2) as ImageView
                            val answerTick3 = view.findViewById(R.id.answerTick3) as ImageView
                            val answerTick4 = view.findViewById(R.id.answerTick4) as ImageView

                            val answer1 = view.findViewById(R.id.answer1) as TextView
                            val answer2 = view.findViewById(R.id.answer2) as TextView
                            val answer3 = view.findViewById(R.id.answer3) as TextView
                            val answer4 = view.findViewById(R.id.answer4) as TextView

                            questionLabel.text = Question

                            answer1.text = A
                            answer2.text = B
                            answer3.text = C
                            answer4.text = D

                            answerObject.put(Question, "")

                            answerView1.setOnClickListener {
                                answerTick1.setImageResource(R.drawable.ic_selected)
                                answerTick2.setImageResource(R.drawable.ic_unselected)
                                answerTick3.setImageResource(R.drawable.ic_unselected)
                                answerTick4.setImageResource(R.drawable.ic_unselected)

                                answerObject.put(Question, A)
                                hideKeyboardFrom(this)
                            }

                            answerView2.setOnClickListener {
                                answerTick1.setImageResource(R.drawable.ic_unselected)
                                answerTick2.setImageResource(R.drawable.ic_selected)
                                answerTick3.setImageResource(R.drawable.ic_unselected)
                                answerTick4.setImageResource(R.drawable.ic_unselected)

                                answerObject.put(Question, B)
                                hideKeyboardFrom(this)
                            }

                            answerView3.setOnClickListener {
                                answerTick1.setImageResource(R.drawable.ic_unselected)
                                answerTick2.setImageResource(R.drawable.ic_unselected)
                                answerTick3.setImageResource(R.drawable.ic_selected)
                                answerTick4.setImageResource(R.drawable.ic_unselected)

                                answerObject.put(Question, C)
                                hideKeyboardFrom(this)
                            }

                            answerView4.setOnClickListener {
                                answerTick1.setImageResource(R.drawable.ic_unselected)
                                answerTick2.setImageResource(R.drawable.ic_unselected)
                                answerTick3.setImageResource(R.drawable.ic_unselected)
                                answerTick4.setImageResource(R.drawable.ic_selected)

                                answerObject.put(Question, D)
                                hideKeyboardFrom(this)
                            }

                            questionView.addView(view)
                        }
                    }
                }

                progressDialog?.dismiss()
                submitButton.visibility = View.VISIBLE
            }

            .addOnFailureListener {
                showToast(this, it.message.toString())
            }

    }

    private val firestoreDatabaseChannels by lazy {
        FirebaseFirestore.getInstance().collection("ListQuestions")
    }

    private val firestoreDatabaseChannelsData by lazy {
        FirebaseFirestore.getInstance().collection("Data")
    }
}