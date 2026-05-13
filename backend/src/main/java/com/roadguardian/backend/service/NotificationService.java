package com.roadguardian.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roadguardian.backend.model.entity.Notification;
import com.roadguardian.backend.repository.NotificationRepository;
import com.roadguardian.backend.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;

	public Notification createNotification(Long userId, String title, String message, Notification.NotificationType type) {
		Notification notification = Notification.builder()
				.user(userRepository.findById(userId).orElseThrow())
				.title(title)
				.message(message)
				.type(type)
				.isRead(false)
				.build();
		return notificationRepository.save(notification);
	}

	public List<Notification> getUserNotifications(Long userId) {
		return notificationRepository.findByUserId(userId);
	}

	public List<Notification> getUnreadNotifications(Long userId) {
		return notificationRepository.findByUserIdAndIsRead(userId, false);
	}

	public void markAsRead(Long notificationId) {
		Notification notification = notificationRepository.findById(notificationId).orElseThrow();
		notification.setIsRead(true);
		notificationRepository.save(notification);
	}

	public void deleteNotification(Long notificationId) {
		notificationRepository.deleteById(notificationId);
	}
}
