package com.bennyv17.river.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.afollestad.materialdialogs.list.listItems
import com.bennyv17.river.R
import com.bennyv17.river.activity.Main2Activity
import com.bennyv17.river.activity.Main3Activity
import com.bennyv17.river.activity.Main3Activity.Companion.PROJECT_DIR
import com.bennyv17.river.callback.RiverInterface
import com.bennyv17.river.item.ProjectItem
import com.bennyv17.river.template.RiveScriptTemplate
import com.bennyv17.river.util.Tool
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import kotlinx.android.synthetic.main.fragment_project.view.*
import java.io.File


class ProjectFragment : Fragment() {

    private var dirPath: String = PROJECT_DIR
    private val projectAdapter: FastItemAdapter<ProjectItem> = FastItemAdapter()
    private lateinit var allScripts: ArrayList<File>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_project, container, false)

        rootView.project_recycler.adapter = projectAdapter

        if ((activity as RiverInterface).isPermissionGranted())
            initAllScripts()

        return rootView
    }

    fun initAllScripts() {
        allScripts = Tool.listDirectory(dirPath, Main3Activity.RIVE_SCRIPT_EXTENSION)

        projectAdapter.clear()
        allScripts.forEach {
            projectAdapter.add(ProjectItem(it.nameWithoutExtension)
                    .withClickListener {

                    }
                    .withLongClickListener {
                        val projectName = it.nameWithoutExtension
                        val items = arrayListOf(getString(R.string.rename), getString(R.string.delete))

                        MaterialDialog(context!!).show {
                            title(text = projectName)
                            listItems(items = items) { dialog, index, text ->
                                when (index) {
                                    0 -> {
                                        MaterialDialog(context).show {
                                            title(text = getString(R.string.rename) + " " + projectName)
                                            input(hint = projectName) { _, input ->

                                                val newFile = File(it.parentFile, input.toString() + Main2Activity.RIVE_SCRIPT_EXTENSION)
                                                it.renameTo(newFile)

                                                initAllScripts()
                                            }
                                        }
                                    }
                                    1 -> {
                                        MaterialDialog(context).show {
                                            title(text = getString(R.string.delete) + " " + projectName)
                                            message(R.string.are_you_sure)
                                            negativeButton(R.string.cancel)
                                            positiveButton(R.string.confirm) { dialog ->

                                                if (it.delete())
                                                    Tool.toast(activity!!, getString(R.string.deleted))
                                                else
                                                    Tool.toast(activity!!, getString(R.string.unable_to_delete))

                                                initAllScripts()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
            )
        }

    }

    fun newScript() {
        MaterialDialog(context!!)
                .title(text = "Create new bot")
                .listItems(items = arrayListOf("New", "From Template")) { dialog, index, text ->
                    when (index) {
                        0 -> {
                            MaterialDialog(context!!)
                                    .title(text = "New Bot Name")
                                    .input { _, input ->
                                        val name = Tool.stripExtension(input.toString())
                                        initAllScripts()
                                    }.show()
                        }
                        1 -> {
                            MaterialDialog(context!!)
                                    .listItems(items = RiveScriptTemplate.getTemplateTitles())
                                    { dialog, index, text ->
                                        MaterialDialog(context!!)
                                                .title(text = "Template Bot Name")
                                                .input { _, input ->
                                                    val temp = RiveScriptTemplate.getTemplate()[index]
                                                    val code = "! version = 2.0\n\n$temp"
                                                    val name = Tool.stripExtension(input.toString())


                                                    initAllScripts()
                                                }.show()
                                    }
                                    .show()
                        }
                    }
                }.show()


    }

    companion object {

        fun newInstance(): ProjectFragment {
            return ProjectFragment()
        }
    }
}