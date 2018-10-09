package com.tvl.trainningapp.model;

/**
 * Created by Shipon on 9/01/2017.
 */
public class DivisionItem {

    public String id;
    public String geo_code;
    public String name;
    public String name_bn;
    public String created_by;
    public String updated_by;
    public String created_at;
    public String updated_at;

    public DivisionItem() {
        super();
    }

    public DivisionItem(String id, String geo_code, String name,
                        String name_bn, String created_by, String updated_by,
                        String created_at, String updated_at) {
        this.id = id;
        this.geo_code = geo_code;
        this.name = name;
        this.name_bn = name_bn;
        this.created_by = created_by;
        this.updated_by = updated_by;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGeo_code() {
        return geo_code;
    }

    public void setGeo_code(String geo_code) {
        this.geo_code = geo_code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName_bn() {
        return name_bn;
    }

    public void setName_bn(String name_bn) {
        this.name_bn = name_bn;
    }

    public String getCreated_by() {
        return created_by;
    }

    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

    public String getUpdated_by() {
        return updated_by;
    }

    public void setUpdated_by(String updated_by) {
        this.updated_by = updated_by;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return name;
    }
}