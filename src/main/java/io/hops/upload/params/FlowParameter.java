package io.hops.upload.params;

public class FlowParameter {

    private long flowChunkNumber; //starts with 1 not 0
    private long flowChunkSize;
    private long flowCurrentChunkSize;
    private long flowTotalSize;
    private String flowIdentifier;
    private String flowFilename;
    private String flowRelativePath;
    private long flowTotalChunks;

    public long getFlowChunkNumber() {
        return flowChunkNumber;
    }

    public long getFlowChunkSize() {
        return flowChunkSize;
    }

    public long getFlowCurrentChunkSize() {
        return flowCurrentChunkSize;
    }

    public long getFlowTotalChunks() {
        return flowTotalChunks;
    }

    public long getFlowTotalSize() {
        return flowTotalSize;
    }

    public String getFlowFilename() {
        return flowFilename;
    }

    public String getFlowIdentifier() {
        return flowIdentifier;
    }

    public String getFlowRelativePath() {
        return flowRelativePath;
    }

    public void setFlowChunkNumber(long flowChunkNumber) {
        this.flowChunkNumber = flowChunkNumber;
    }

    public void setFlowChunkSize(long flowChunkSize) {
        this.flowChunkSize = flowChunkSize;
    }

    public void setFlowCurrentChunkSize(long flowCurrentChunkSize) {
        this.flowCurrentChunkSize = flowCurrentChunkSize;
    }

    public void setFlowFilename(String flowFilename) {
        this.flowFilename = flowFilename;
    }

    public void setFlowIdentifier(String flowIdentifier) {
        this.flowIdentifier = flowIdentifier;
    }

    public void setFlowRelativePath(String flowRelativePath) {
        this.flowRelativePath = flowRelativePath;
    }

    public void setFlowTotalChunks(long flowTotalChunks) {
        this.flowTotalChunks = flowTotalChunks;
    }

    public void setFlowTotalSize(long flowTotalSize) {
        this.flowTotalSize = flowTotalSize;
    }
}
