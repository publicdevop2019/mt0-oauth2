//   Copyright 2012,2013 Vaughn Vernon
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package com.mt.common.port.adapter.persistence;

import com.mt.common.domain.model.DomainEvent;
import com.mt.common.notification.PublishedEventTracker;
import com.mt.common.notification.PublishedEventTrackerRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface HibernatePublishedEventTrackerRepository extends PublishedEventTrackerRepository, JpaRepository<PublishedEventTracker, Long> {
    default PublishedEventTracker publishedNotificationTracker() {
        Iterable<PublishedEventTracker> all = findAll();
        List<PublishedEventTracker> objects = new ArrayList<>(1);
        all.forEach(objects::add);
        return objects.isEmpty() ? new PublishedEventTracker() : objects.get(0);
    }

    default void trackMostRecentPublishedNotification(PublishedEventTracker tracker, List<DomainEvent> events) {
        int lastIndex = events.size() - 1;
        if (lastIndex >= 0) {
            long mostRecentId = events.get(lastIndex).getId();
            tracker.setLastPublishedEventId(mostRecentId);
            save(tracker);
        }
    }

}
