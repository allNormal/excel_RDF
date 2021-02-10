package com.java.fto.mapper.RestrictionMapper;

import com.java.fto.entity.EndpointEntity.Receiver.WorksheetReceiver;
import com.java.fto.entity.Restriction.Restriction;

import java.util.List;

public interface restrictionMap {

    Restriction payloadToRestriction(List<WorksheetReceiver> ws, String type);
}
