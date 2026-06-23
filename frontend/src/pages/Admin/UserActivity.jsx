import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table';
import { Button } from '@/components/ui/button';
import { getUsers, approveUser, rejectUser } from '@/State/AdminUsers/Action';
import { isAdmin } from '@/utils/roleUtils';
import { toast } from 'sonner';

const UserActivity = () => {
  const dispatch = useDispatch();
  const { adminUsers, auth } = useSelector((store) => store);
  const jwt = auth.jwt || localStorage.getItem('jwt');

  useEffect(() => {
    if (isAdmin(auth.user)) {
      dispatch(getUsers(jwt));
    }
  }, [dispatch, jwt, auth.user]);

  const handleApprove = (id) => {
    dispatch(approveUser(id, jwt)).then(() => toast.success('User Approved'));
  };

  const handleReject = (id) => {
    dispatch(rejectUser(id, jwt)).then(() => toast.success('User Rejected'));
  };

  return (
    <div className="p-5">
      <h1 className="text-2xl font-bold mb-5">User Approvals & Activity</h1>
      <div className="rounded-md border border-border/40">
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>User ID</TableHead>
              <TableHead>Full Name</TableHead>
              <TableHead>Email</TableHead>
              <TableHead>Verified</TableHead>
              <TableHead>Approval Status</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {adminUsers.users?.map((user) => (
              <TableRow key={user.id}>
                <TableCell>{user.id}</TableCell>
                <TableCell>{user.fullName}</TableCell>
                <TableCell>{user.email}</TableCell>
                <TableCell>
                  <span
                    className={`px-2 py-1 rounded-full text-xs font-bold ${
                      user.verified
                        ? 'bg-green-500/20 text-green-500'
                        : 'bg-red-500/20 text-red-500'
                    }`}
                  >
                    {user.verified ? 'VERIFIED' : 'PENDING'}
                  </span>
                </TableCell>
                <TableCell>
                  <span
                    className={`px-2 py-1 rounded-full text-xs font-bold ${
                      user.approvedByAdmin
                        ? 'bg-green-500/20 text-green-500'
                        : 'bg-yellow-500/20 text-yellow-500'
                    }`}
                  >
                    {user.approvedByAdmin ? 'APPROVED' : 'WAITING'}
                  </span>
                </TableCell>
                <TableCell className="text-right">
                  {!user.approvedByAdmin ? (
                    <Button
                      onClick={() => handleApprove(user.id)}
                      className="bg-green-600 hover:bg-green-700 text-white mr-2"
                      size="sm"
                    >
                      Approve
                    </Button>
                  ) : (
                    <Button
                      onClick={() => handleReject(user.id)}
                      className="bg-red-600 hover:bg-red-700 text-white"
                      size="sm"
                      variant="destructive"
                    >
                      Block
                    </Button>
                  )}
                </TableCell>
              </TableRow>
            ))}
            {adminUsers.users?.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-5 text-gray-500">
                  No users found
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </div>
    </div>
  );
};

export default UserActivity;
