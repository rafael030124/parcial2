package edu.pucmm.icc352.services;

import edu.pucmm.icc352.models.Registration;
import edu.pucmm.icc352.repositories.AttendanceRepository;
import edu.pucmm.icc352.repositories.EventRepository;
import edu.pucmm.icc352.repositories.RegistrationRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class StatsService {

    private final EventRepository        evtRepo = new EventRepository();
    private final RegistrationRepository regRepo = new RegistrationRepository();
    private final AttendanceRepository   attRepo = new AttendanceRepository();

    public Map<String, Object> getEventStats(Long eventId) {
        long totalInscritos  = evtRepo.countRegistrations(eventId);
        long totalAsistentes = evtRepo.countAttendance(eventId);
        double porcentaje    = totalInscritos == 0 ? 0
                : Math.round((totalAsistentes * 100.0 / totalInscritos) * 10) / 10.0;
        long ausentes = Math.max(totalInscritos - totalAsistentes, 0);

        Map<String, Object> stats = new LinkedHashMap<>();
        // English keys consumed by current frontend dashboards.
        stats.put("totalRegistrations", totalInscritos);
        stats.put("totalAttendances",  totalAsistentes);
        stats.put("attendanceRate",    porcentaje);
        stats.put("absentees",         ausentes);

        // Spanish aliases kept for compatibility.
        stats.put("totalInscritos",       totalInscritos);
        stats.put("totalAsistentes",      totalAsistentes);
        stats.put("porcentajeAsistencia", porcentaje);
        stats.put("inscripcionesPorDia",  getRegistrationsByDay(eventId));
        stats.put("asistenciaPorHora",    getAttendanceByHour(eventId));
        return stats;
    }

    private List<Map<String, Object>> getRegistrationsByDay(Long eventId) {
        List<Registration> regs = regRepo.findByEvent(eventId);
        Map<String, Long> byDay = new TreeMap<>();
        for (Registration r : regs) {
            String day = r.getRegisteredAt().toLocalDate().toString();
            byDay.merge(day, 1L, Long::sum);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        byDay.forEach((day, count) -> {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("date",  day);
            entry.put("count", count);
            result.add(entry);
        });
        return result;
    }

    private List<Map<String, Object>> getAttendanceByHour(Long eventId) {
        List<Object[]> rows = attRepo.countByHour(eventId);
        List<Map<String, Object>> result = new ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> entry = new LinkedHashMap<>();
            entry.put("hour",  row[0]);
            entry.put("count", row[1]);
            result.add(entry);
        }
        return result;
    }
}