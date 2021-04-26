package com.team06.focuswork.ui.overview

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.team06.focuswork.MainActivity
import com.team06.focuswork.R
import com.team06.focuswork.data.Task
import com.team06.focuswork.databinding.FragmentOverviewBinding
import com.team06.focuswork.model.TasksViewModel
import com.team06.focuswork.ui.taskdetails.TaskdetailsFragment


class OverviewFragment : Fragment() {

    private val tasksViewModel: TasksViewModel by activityViewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: FragmentOverviewBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentOverviewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = binding.recyclerView
        recyclerView.adapter = TaskAdapter(requireContext(), this)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        createNotifChannel()
        binding.notifButton.setOnClickListener(this::sendNotif)

    }

    private fun createNotifChannel() {
        // based off this tutorial
        // https://www.youtube.com/watch?v=B5dgmvbrHgs

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Timer finished"
            val descriptionText = "The timer for your task has finished."
            val important = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("TIMER_NOTIF_ID", name, important).apply {
                description = descriptionText;
            }
            val notificationManager = getSystemService(requireContext(),
                    NotificationManager::class.java) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotif(view: View) {
        // based off this tutorial
        // out first notification, navigates back to app by clicking on it
        // https://www.youtube.com/watch?v=B5dgmvbrHgs
        val intent = Intent(requireContext(), MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
                PendingIntent.getActivity(requireContext(), 0, intent, 0)

        val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager
                .TYPE_NOTIFICATION)

        val builder = NotificationCompat.Builder(requireContext(), "TIMER_NOTIF_ID")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Your task has finished!")
                .setContentText("The task {...} you have set has finished.")
                .setStyle(NotificationCompat.BigTextStyle().bigText("This text is so much longer than the original message."))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(notificationSound)

        with(NotificationManagerCompat.from(requireContext())) {
            notify(101, builder.build())
        }
    }

    fun onClickTaskItem(task: Task) {
        val taskdetailsFragment = TaskdetailsFragment()
        tasksViewModel.setSelectedTask(task, requireContext())
        (activity as MainActivity).switchFragments(taskdetailsFragment, R.id.fragment_container_overview)
    }
}