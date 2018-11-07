//package com.bennyv17.river.activity
//
//import agency.tango.materialintroscreen.MaterialIntroActivity
//import agency.tango.materialintroscreen.MessageButtonBehaviour
//import agency.tango.materialintroscreen.SlideFragmentBuilder
//import android.os.Bundle
//import android.view.View
//import android.widget.ImageButton
//import com.bennyv17.river.R
//
//class IntroActivity : MaterialIntroActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        //val overScrollLayout = findViewById<View>(agency.tango.materialintroscreen.R.id.view_pager_slides) as OverScrollViewPager
//        //val viewPager = overScrollLayout.overScrollView
//
//        addSlide(SlideFragmentBuilder()
//                .backgroundColor(R.color.colorPrimary)
//                .buttonsColor(R.color.colorAccent)
//                .image(R.drawable.intro_0)
//                .title("River -> RiveScript")
//                .description("River is an IDE for RiveScript, which is an open source, lightweight and text-based chatbot framework.")
//                .build()
//                ,
//                MessageButtonBehaviour(View.OnClickListener {
//                    //viewPager.moveToNextPage()
//                }, "Next")
//        )
//
//        addSlide(SlideFragmentBuilder()
//                .backgroundColor(R.color.colorPrimary)
//                .buttonsColor(R.color.colorAccent)
//                .image(R.drawable.intro_1)
//                .title("Run, Test, Play Instantly")
//                .description("River is bundled with the Java version of the RiveScript interpreter, which is also open source, with intuitive UI, you can test your bot easily.")
//                .build(),
//                MessageButtonBehaviour(View.OnClickListener {
//                    finish()
//                }, "Get Started")
//        )
//        hideBackButton()
//
//        val nextButton = findViewById<View>(agency.tango.materialintroscreen.R.id.button_next) as ImageButton
//        nextButton.visibility = View.INVISIBLE
//    }
//
//}
