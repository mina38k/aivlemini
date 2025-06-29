package aivlemini.infra;

import aivlemini.domain.*;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//<<< Clean Arch / Inbound Adaptor

@RestController
@RequestMapping(value="/points")
@Transactional
public class PointController {

    @Autowired
    PointRepository pointRepository;

    // 특정 User의 현재 Point를 조회하는 DTO (Read Model)
    @GetMapping("/{userId}")
    public GetPointsQuery getPointByUser(@PathVariable Long userId) {
        // 특정 userId가 가지고 있는 point를 가져옴.
        Point point = pointRepository.findByUserId(new UserId(userId));

        if (point == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "포인트 정보 없음");
        }

        // Aggregate → DTO 변환
        GetPointsQuery result = new GetPointsQuery();
        result.setId(point.getId());
        result.setPoint(point.getPoint());
        result.setUserId(point.getUserId());
        result.setApplyingId(point.getApplyingId());

        return result;
    }

    // 포인트 구매 Command
    @PostMapping("/buy")
    public void buyPoint(@RequestBody BuyPointCommand cmd) {
        Point point = pointRepository.findByUserId(new UserId(cmd.getUserId()));

        if (point == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "포인트 정보 없음");
        }

        point.buyPoint(cmd);
        pointRepository.save(point);
    }
}
}


//>>> Clean Arch / Inbound Adaptor
