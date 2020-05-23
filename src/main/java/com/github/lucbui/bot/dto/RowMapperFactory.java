package com.github.lucbui.bot.dto;

import org.springframework.jdbc.core.RowMapper;

public class RowMapperFactory {
    public static final RowMapper<CountryDto> COUNTRY =
            (resultSet, i) -> new CountryDto(
                    resultSet.getString(1),
                    resultSet.getString(2));

    public static final RowMapper<StateProvinceDto> STATE_PROVINCE =
            (resultSet, i) -> new StateProvinceDto(
                    resultSet.getString(1),
                    resultSet.getString(2),
                    resultSet.getString(3));

    public static final RowMapper<CityDto> CITY =
            (resultSet, i) -> new CityDto(
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3),
                    resultSet.getString(4),
                    resultSet.getBigDecimal(5),
                    resultSet.getBigDecimal(6));
}
