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

const initialState = {
  users: [],
  loading: false,
  error: null,
};

export const adminUserReducer = (state = initialState, action) => {
  switch (action.type) {
    case GET_USERS_REQUEST:
    case APPROVE_USER_REQUEST:
    case REJECT_USER_REQUEST:
      return { ...state, loading: true, error: null };
    case GET_USERS_SUCCESS:
      return { ...state, loading: false, users: action.payload, error: null };
    case APPROVE_USER_SUCCESS:
    case REJECT_USER_SUCCESS:
      return {
        ...state,
        loading: false,
        users: state.users.map((user) =>
          user.id === action.payload.id ? action.payload : user
        ),
        error: null,
      };
    case GET_USERS_FAILURE:
    case APPROVE_USER_FAILURE:
    case REJECT_USER_FAILURE:
      return { ...state, loading: false, error: action.payload };
    default:
      return state;
  }
};
