package com.amine.mytfg

import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SettingsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AppListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!hasUsageStatsPermission()) {
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        loadApps()
        return view
    }

    @SuppressLint("ServiceCast")
    private fun hasUsageStatsPermission(): Boolean {
        val appOps = context?.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
        val mode = appOps?.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(), requireContext().packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }
    private fun loadApps() {
        val pm = context?.packageManager
        val apps = pm?.getInstalledApplications(PackageManager.GET_META_DATA)?.filterNot { it.flags and ApplicationInfo.FLAG_SYSTEM != 0 }
        adapter = AppListAdapter(apps ?: listOf(), pm!!)
        recyclerView.adapter = adapter
    }

    class AppListAdapter(private val apps: List<ApplicationInfo>, private val packageManager: PackageManager) :
        RecyclerView.Adapter<AppListAdapter.AppViewHolder>() {
        private val selectedApps = mutableMapOf<String, Boolean>()

        class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val appName: TextView = itemView.findViewById(R.id.appNameTextView)
            val appCheckBox: CheckBox = itemView.findViewById(R.id.appSelectedCheckBox)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_app, parent, false)
            return AppViewHolder(view)
        }

        override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
            val app = apps[position]
            holder.appName.text = app.loadLabel(packageManager).toString()
            holder.appCheckBox.isChecked = selectedApps[app.packageName] ?: false

            holder.appCheckBox.setOnCheckedChangeListener { _, isChecked ->
                selectedApps[app.packageName] = isChecked
            }
        }

        override fun getItemCount() = apps.size

        fun getSelectedApps(): List<String> {
            return selectedApps.filter { it.value }.map { it.key }
        }
    }
}
