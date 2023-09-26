import { styled } from "styled-components";
import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";

import DirectoryEditBox from "../components/DirectoryEditBox";

import { URL } from "../data/constants";

import authLoginCheck from "../utils/authLoginCheck";

const DirectoryModifyPage = () => {
  const isLogin = authLoginCheck();
  if (!isLogin) {
    return window.location.replace("/login");
  }

  const navigate = useNavigate();

  const { categoryId } = useParams();
  const { accessToken } = JSON.parse(localStorage.getItem("localUser"));
  const [category, setCategory] = useState("");

  useEffect(() => {
    getCategory();
  }, [categoryId]);

  const changeCategory = (text) => {
    setCategory(text);
  };

  const getCategory = async () => {
    const { data } = await axios.get(`${URL}/todoStorages/${categoryId}`, {
      headers: { Authorization: `Bearer ${accessToken}` },
    });

    setCategory(data.data.category);
  };

  const patchCategory = async () => {
    if (category.length === 0) {
      return alert("디렉토리명은 필수값입니다.");
    }
    if (category.length > 20) {
      return alert("디렉토리명은 20자 이내로 작성해주세요.");
    }

    await axios.patch(
      `${URL}/todoStorages/${categoryId}`,
      { category },
      {
        headers: { Authorization: `Bearer ${accessToken}` },
      },

      navigate("/directory"),
    );
  };

  return (
    <>
      <EditWrapper>
        <EditSection>
          <DirectoryEditBox
            EditPage={false}
            category={category}
            changeCategory={changeCategory}
            postCategory={patchCategory}
          />
        </EditSection>
      </EditWrapper>
    </>
  );
};

export default DirectoryModifyPage;

const EditWrapper = styled.div`
  height: 100%;

  display: flex;
  flex-direction: column;
  align-items: center;

  font-family: "HakgyoansimWoojuR";
`;

const EditSection = styled.section`
  width: 100%;
  max-width: 430px;
  height: 100%;

  background-color: #ffffff;

  padding-top: 80px;
  padding-bottom: 130px;

  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  overflow: auto;
`;
