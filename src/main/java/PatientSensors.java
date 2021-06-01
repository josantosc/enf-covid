public class PatientSensors {
    Integer resp;
    Integer hr;
    Integer spo2;
    Integer pulse;

    public PatientSensors(Integer resp, Integer hr, Integer spo2, Integer pulse) {
        this.resp = resp;
        this.hr = hr;
        this.spo2 = spo2;
        this.pulse = pulse;
    }

    public Integer getResp() {
        return resp;
    }

    public void setResp(Integer resp) {
        this.resp = resp;
    }

    public Integer getHr() {
        return hr;
    }

    public void setHr(Integer hr) {
        this.hr = hr;
    }

    public Integer getSpo2() {
        return spo2;
    }

    public void setSpo2(Integer spo2) {
        this.spo2 = spo2;
    }

    public Integer getPulse() {
        return pulse;
    }

    public void setPulse(Integer pulse) {
        this.pulse = pulse;
    }

    @Override
    public String toString() {
        return String.format("resp: %s, hr: %s, spo2: %s, pulse: %s",
                this.resp, this.hr, this.spo2, this.pulse);
    }
}
