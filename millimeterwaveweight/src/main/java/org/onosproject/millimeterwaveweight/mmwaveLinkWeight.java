package org.onosproject.millimeterwaveweight;


import org.onosproject.net.topology.LinkWeight;
import org.onosproject.net.topology.TopologyEdge;


class mmwaveLinkWeight implements LinkWeight {

    @Override
    public double weight(TopologyEdge edge) {

        //AnnotationKeys
        //This can help us to define cost function by annotations
        String v = edge.link().annotations().value("Ps");
        try {
            return v != null ? 1/Double.parseDouble(v) : 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}