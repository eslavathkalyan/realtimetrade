import api from '@/config/api';
import {
  GET_USERS_REQUEST,
  GET_USERS_SUCCESS,
  GET_USERS_FAILURE,
  APPROVE_USER_REQUEST,
  APPROVE_USER_SUCCESS,
  APPROVE_USER_FAILURE,
  REJECT_USER_REQUEST,
  REJECT_USER_SUCCESS,
  REJECT_USER_FAILURE,
} from './ActionType';

export const getUsers = (jwt) => async (dispatch) => {
  dispatch({ type: GET_USERS_REQUEST });
  try {
    const response = await api.get('/api/admin/users', {
      headers: { Authorization: `Bearer ${jwt}` },
    });
    dispatch({ type: GET_USERS_SUCCESS, payload: response.data });
  } catch (error) {
    dispatch({ type: GET_USERS_FAILURE, payload: error.message });
  }
};

export const approveUser = (id, jwt) => async (dispatch) => {
  dispatch({ type: APPROVE_USER_REQUEST });
  try {
    const response = await api.post(`/api/admin/users/${id}/approve`, {}, {
      headers: { Authorization: `Bearer ${jwt}` },
    });
    dispatch({ type: APPROVE_USER_SUCCESS, payload: response.data });
  } catch (error) {
    dispatch({ type: APPROVE_USER_FAILURE, payload: error.message });
  }
};

export const rejectUser = (id, jwt) => async (dispatch) => {
  dispatch({ type: REJECT_USER_REQUEST });
  try {
    const response = await api.post(`/api/admin/users/${id}/reject`, {}, {
      headers: { Authorization: `Bearer ${jwt}` },
    });
    dispatch({ type: REJECT_USER_SUCCESS, payload: response.data });
  } catch (error) {
    dispatch({ type: REJECT_USER_FAILURE, payload: error.message });
  }
};
